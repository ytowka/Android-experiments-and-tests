package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.danilkha.composeapptemplate.ui.theme.Shapes
import kotlin.math.roundToInt

@Composable
fun ImageCropper(
    image: String,
    clipRatio: Float = 1.5f
) {
    val imageBitmap by rememberImageBitmap(image)

    val sensivity = 0.0015f

    BoxWithConstraints {
        val windowSize = with(LocalDensity.current) {
            Size(maxWidth.toPx(), maxHeight.toPx())
        }
        val viewport = rememberViewport()

        LaunchedEffect(imageBitmap, windowSize){
            imageBitmap?.let {
                viewport.imageSize = it.size
            }
            viewport.clippingRect = ClippingRect.clippingRect(windowSize,clipRatio)
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
                rotate(degrees = angle/(2 * Math.PI).toFloat()*360, Offset.Zero.toWindowOffset()){
                    val size = imageSize.toWindowSize().roundToInt()
                    val offset = imageTopLeft.toWindowOffset().roundToInt()
                    imageBitmap?.let {
                        drawImage(
                            image = it.imageBitmap,
                            dstOffset = offset,
                            dstSize =  size,
                        )
                    }

                }


                //drawRect(color = Color.Gray, topLeft = imageTopLeft.toWindowOffset(), imageSize)
                with(clippingRect.rect){
                    drawCircle(color = Color.Blue, radius = 10f, center = topLeft.toViewportOffset().rotate(-angle).toWindowOffset())
                    drawCircle(color = Color.Blue, radius = 10f, center = topRight.toViewportOffset().rotate(-angle).toWindowOffset())
                    drawCircle(color = Color.Blue, radius = 10f, center = bottomLeft.toViewportOffset().rotate(-angle).toWindowOffset())
                    drawCircle(color = Color.Blue, radius = 10f, center = bottomRight.toViewportOffset().rotate(-angle).toWindowOffset())
                }

                with(clippingRect.circumscribedRect(angle).rect){
                    drawCircle(color = Color.Red, radius = 10f, center = topLeft)
                    drawCircle(color = Color.Red, radius = 10f, center = topRight)
                    drawCircle(color = Color.Red, radius = 10f, center = bottomLeft)
                    drawCircle(color = Color.Red, radius = 10f, center = bottomRight)
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

        }
        ViewportInfo(viewport)

        Spacer(
            modifier = Modifier
                .clip(Shapes.medium)
                .padding(16.dp)
                .height(40.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = listOf(Color.Gray, Color.LightGray), tileMode = TileMode.Mirror)
                )
                .pointerInput(Unit){
                    detectHorizontalDragGestures { change, dragAmount ->
                         viewport.rotate(dragAmount*sensivity)
                    }
                }
        )
    }
}

@Composable
fun ViewportInfo(viewport: Viewport){
    with(viewport){
        Text(buildString {
            appendLine("clip ${clippingRect.offset}, image $imageSize")
            appendLine()
            appendLine("viewport clip: ${clippingRect.offset.toViewportOffset()}")
            appendLine("viewport clip br: ${(clippingRect.offset + clippingRect.size).toViewportOffset() - imageSize} ")
            appendLine()
            appendLine("offset: $center")
            appendLine("scale: $scale")
            appendLine()
            appendLine("rotate: ${angle/(2 * Math.PI) * 360}")
        }, color = Color.White)
    }
}

fun Size.roundToInt() = IntSize(width.roundToInt(), height.roundToInt())

operator fun IntOffset.plus(intSize: IntSize) = IntOffset(x+intSize.width, y + intSize.height)
//operator fun Size.div(f: Float) = Size(width/f, height/f)
fun Offset.roundToInt() = IntOffset(x.roundToInt(), y.roundToInt())