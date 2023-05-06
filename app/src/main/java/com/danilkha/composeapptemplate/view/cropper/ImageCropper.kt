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
    image: String
) {
    val imageBitmap by rememberImageBitmap(image)

    BoxWithConstraints {
        val windowSize = with(LocalDensity.current) {
            Size(maxWidth.toPx(), maxHeight.toPx())
        }
        val viewport = rememberViewport(windowSize)

        LaunchedEffect(imageBitmap){
            imageBitmap?.let {
                viewport.imageSize = it.size
            }
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
                imageBitmap?.let {
                    drawImage(
                        image = it.imageBitmap,
                        dstOffset = Offset.Zero.toWindowOffset().roundToInt(),
                        dstSize =  imageSize.toWindowSize().roundToInt(),
                    )
                }
            }
            val clippingRect = viewport.clippingRect()
            clipRect(
                clippingRect.second.x,
                clippingRect.second.y,
                clippingRect.second.x + clippingRect.first.width,
                clippingRect.second.y + clippingRect.first.height,
                clipOp = ClipOp.Difference
            ) {
                drawRect(
                    color = Color.Gray.copy(alpha = 0.6f),
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
            appendLine("clip: ${clippingRect().second}")
            appendLine("viewport clip: ${clippingRect().second.toViewportOffset()}")
            appendLine("offset: $offset")
            appendLine("scale: $scale")
        })
    }
}

fun Size.roundToInt() = IntSize(width.roundToInt(), height.roundToInt())
fun Offset.roundToInt() = IntOffset(x.roundToInt(), y.roundToInt())