package com.danilkha.composeapptemplate.view.cropper

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.danilkha.composeapptemplate.R
import com.danilkha.composeapptemplate.ui.TextButton
import com.danilkha.composeapptemplate.ui.theme.Shapes

@Composable
fun ImageCropper(
    bitmap: Bitmap,
    clipRatio: Float = 1.5f,
    onImageSave: (cropRect: Rect, angle: Float) -> Unit,
    rotation: Boolean = true,
    preferredAngle: Float = 0f,
) {
    val sensivity = 0.0015f
    val clipColor = MaterialTheme.colors.background

    BoxWithConstraints {

        val windowSize = with(LocalDensity.current) {
            Size(maxWidth.toPx(), maxHeight.toPx())
        }
        val viewport = rememberViewport(
            clippingRect = ClippingRect.clippingRect(windowSize,clipRatio),
            imageSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
        )

        LaunchedEffect(preferredAngle){
            viewport.setRotate(preferredAngle)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit){
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        viewport.zoom(zoom, centroid)
                        viewport.translate(pan)
                    }
                },
        ){
            with(viewport){
                rotate(degrees = angle.toDeg(), clippingRect.rect.center){
                    val size = imageSize.toWindowSize().roundToInt()
                    val offset = imageTopLeft.toWindowOffset().roundToInt()
                    drawImage(
                        image = bitmap.asImageBitmap(),
                        dstOffset = offset,
                        dstSize =  size,
                    )

                }
            }
            val clippingRect = viewport.clippingRect
            clipRect(
                clippingRect.offset.x,
                clippingRect.offset.y,
                clippingRect.offset.x + clippingRect.size.width,
                clippingRect.offset.y + clippingRect.size.height,
                clipOp = ClipOp.Difference
            ) {
                drawRect(
                    color = clipColor.copy(alpha = 0.7f),
                )
            }
        }
        Controls(
            rotation = rotation,
            onSave = {
                with(viewport){
                    onImageSave(getCropArea(), angle.toDeg(),)
                }
            },
            onReset = {
                viewport.bringToCenter()
            },
            onRotate = {
                viewport.rotate(it*sensivity)
            },
            onRotate90 = {
                viewport.rotate(-Math.PI.toFloat()/2)
            }
        )
    }
}

@Composable
fun BoxScope.Controls(
    rotation: Boolean,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onRotate: (Float) -> Unit,
    onRotate90: () -> Unit,
){
    Column(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomCenter),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if(rotation){
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .height(40.dp)
                        .weight(1f)
                        .background(
                            brush = Brush.horizontalGradient(colors = listOf(Color.Gray, Color.LightGray), tileMode = TileMode.Mirror)
                        )
                        .pointerInput(Unit){
                            detectHorizontalDragGestures { change, dragAmount ->
                                onRotate(-dragAmount)
                            }
                        }
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth()){
            TextButton(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "reset",
                onClick = onReset,
                color = Color.Gray,
            )
            Image(
                modifier = Modifier
                    .clickable {
                        onRotate90()
                    }
                    .padding(8.dp)
                    .align(alignment = Alignment.Center)
                ,
                painter = painterResource(R.drawable.baseline_rotate_90_degrees_ccw_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
            )
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = "crop",
                onClick = onSave,
            )
        }
    }
}

fun DrawScope.OffsetHints(viewport: Viewport){
    with(viewport){
        drawLine(
            color = Color.Red,
            start = clippingRect.rect.center.toViewportOffset().let{
                (-it).rotate(angle) + it
            }.toWindowOffset(),
            end = clippingRect.rect.center
            ,
            strokeWidth = 5f,
        )

        val clippingRectTopLeft = clippingRect.rect.topLeft.toViewportOffset()

        drawLine(
            color = Color.Blue,
            start = imageTopLeft.toWindowOffset(),
            end = clippingRectTopLeft.toWindowOffset(),
            strokeWidth = 5f,
        )

        drawLine(
            color = Color.Green,
            start = imageTopLeft.toWindowOffset(),
            end = clippingRectTopLeft.toWindowOffset(),
            strokeWidth = 5f,
        )

        drawLine(
            color = Color.Green,
            start = Offset.Zero,
            end = (clippingRect.offset - rotatedImageTopLeft - center).toWindowOffset() ,
            strokeWidth = 5f,
        )
    }
}

@Composable
fun ViewportInfo(viewport: Viewport){
    with(viewport){
        Text(text = buildString {
            appendLine("clip ${clippingRect.offset}, image $imageSize")
            appendLine()
            appendLine("viewport clip: ${clippingRect.offset.toViewportOffset()}")
            //appendLine("viewport clip br: ${(clippingRect.offset + clippingRect.size).toViewportOffset() - imageSize} ")
            //appendLine()
            //appendLine("offset: $center")
            appendLine("scale: $scale")
            appendLine("rotate: ${angle/(2 * Math.PI) * 360}")
            appendLine()
        }, color = Color.White)
    }
}