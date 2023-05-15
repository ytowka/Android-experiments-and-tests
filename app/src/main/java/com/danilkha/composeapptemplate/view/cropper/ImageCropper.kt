package com.danilkha.composeapptemplate.view.cropper

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.danilkha.composeapptemplate.ui.theme.Shapes

@Composable
fun ImageCropper(
    bitmap: Bitmap,
    clipRatio: Float = 1.5f,
    onImageSave: (Offset, Size, angle: Float) -> Unit,
) {
    val sensivity = 0.0015f

    BoxWithConstraints {

        val windowSize = with(LocalDensity.current) {
            Size(maxWidth.toPx(), maxHeight.toPx())
        }
        val viewport = rememberViewport(
            clippingRect = ClippingRect.clippingRect(windowSize,clipRatio),
            imageSize = Size(bitmap.width.toFloat(), bitmap.height.toFloat())
        )

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
                    color = Color.Black.copy(alpha = 0.6f),
                )
            }
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

                val viewPortTopLeft = clippingRect.toLocal(center).rect.center.let{
                    (-it).rotate(angle) + it - ClippingRect(imageSize, Offset.Zero).circumscribedRect(angle).size/2f
                }

                val clippingRectTopLeft = clippingRect.rect.topLeft.toViewportOffset()

                drawLine(
                    color = Color.Blue,
                    start = viewPortTopLeft.toWindowOffset(),
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
                    end = (clippingRectTopLeft - viewPortTopLeft - center).toWindowOffset(),
                    strokeWidth = 5f,
                )
            }
        }
        ViewportInfo(viewport)

        Row(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text(
                modifier = Modifier
                    .clickable {
                        viewport.rotate((Math.PI/2).toFloat())
                    }
                    .padding(8.dp),
                text = "90",
            )
            Spacer(
                modifier = Modifier
                    .clip(Shapes.medium)
                    .padding(16.dp)
                    .height(40.dp)
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(colors = listOf(Color.Gray, Color.LightGray), tileMode = TileMode.Mirror)
                    )
                    .pointerInput(Unit){
                        detectHorizontalDragGestures { change, dragAmount ->
                            viewport.rotate(dragAmount*sensivity)
                        }
                    }
            )
            FloatingActionButton(
                onClick = {
                    with(viewport){
                        val viewPortTopLeft = clippingRect.toLocal(center).rect.center.let{
                            (-it).rotate(angle) + it - ClippingRect(imageSize, Offset.Zero).circumscribedRect(angle).size/2f
                        }

                        val clippingRectTopLeft = clippingRect.rect.topLeft.toViewportOffset()
                        onImageSave(
                            clippingRectTopLeft - viewPortTopLeft,
                            clippingRect.size.toViewportSize(),
                            angle.toDeg()
                        )
                    }
                },
                content = {
                    Icon(Icons.Default.Check, null)
                }
            )
        }
    }
}

@Composable
fun ViewportInfo(viewport: Viewport){
    with(viewport){
        Text(buildString {
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