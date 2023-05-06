package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
fun ImageCropper(
    image: String,
    clipRatio: Float = 1.5f
) {
    val imageBitmap by rememberImageBitmap(image)

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
            drawRect(color = Color.Gray)
            with(viewport){
                imageBitmap?.let {
                    drawImage(
                        image = it.imageBitmap,
                        dstOffset = Offset.Zero.toWindowOffset().roundToInt(),
                        dstSize =  imageSize.toWindowSize().roundToInt(),
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
        }
        ViewportInfo(viewport)
    }
}

@Composable
fun ViewportInfo(viewport: Viewport){
    with(viewport){
        Text(buildString {
            appendLine("clip: ${clippingRect.offset}")
            appendLine()
            appendLine("viewport clip: ${clippingRect.offset.toViewportOffset()}")
            appendLine("viewport clip br: ${(clippingRect.offset + clippingRect.size).toViewportOffset() - imageSize} ")
            appendLine()
            appendLine("offset: $offset")
            appendLine("scale: $scale")
        })
    }
}

fun Size.roundToInt() = IntSize(width.roundToInt(), height.roundToInt())
fun Offset.roundToInt() = IntOffset(x.roundToInt(), y.roundToInt())