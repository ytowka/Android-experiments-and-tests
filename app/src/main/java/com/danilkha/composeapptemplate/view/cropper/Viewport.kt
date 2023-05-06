package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.max

@Stable
class Viewport(
    windowSize: Size,
    imageSize: Size,
    center: Offset = Offset(0f,0f)
) {


    @WindowDimension var windowSize: Size by mutableStateOf(windowSize, structuralEqualityPolicy())

    @WindowDimension var imageSize: Size by mutableStateOf(imageSize, structuralEqualityPolicy())

    @ViewportDimension var offset: Offset by mutableStateOf(center, structuralEqualityPolicy())


    var scale: Float by mutableStateOf(1f)

    var clippingRectRatio: Float by mutableStateOf(1.5f)

    fun zoom(scale: Float, @WindowDimension anchor: Offset){
        val viewportAnchor = anchor.toViewportOffset()
        this.scale = (this.scale * scale).coerceIn(minScale, maxScale)
        val newViewportAnchor = anchor.toViewportOffset()
        offset = (offset - (viewportAnchor - newViewportAnchor)).rectLimited()
    }

    fun translate(@WindowDimension delta: Offset){
        offset = (offset + delta / scale).rectLimited()
    }

    val minScale: Float
        get(){
            val clippingRectSize = clippingRect().first;
            return max(
                clippingRectSize.width / imageSize.width,
                clippingRectSize.height / imageSize.height,
            )
        }

    val maxScale: Float = 5f

    fun Offset.rectLimited(): Offset{
        var (clipSize, clipOffset) = clippingRect();
        clipSize = clipSize.toViewportSize()
        clipOffset = clipOffset.toLocalOffset(this)

        var newX = x
        var newY = y

        if(clipOffset.x < 0){
            newX = x + clipOffset.x
        }

        if(clipOffset.y < 0){
            newY = y + clipOffset.y
        }

        /*if(clipOffset.x + clipSize.width - imageSize.width * scale > 0){
            newX = x + clipOffset.x + clipSize.width - imageSize.width * scale
        }

        if(clipOffset.y + clipSize.height - imageSize.height * scale > 0){
            newY = y + clipOffset.y + clipSize.height - imageSize.height * scale
        }*/

        return Offset(newX, newY)
    }


    fun Offset.toViewportOffset(): Offset{
        return Offset(
            x = x / scale - offset.x,
            y = y / scale - offset.y
        )
    }

    fun Offset.toWindowOffset(): Offset{
        return Offset(
            x = (x + offset.x) * scale,
            y = (y + offset.y) * scale,
        )
    }

    fun Offset.toLocalOffset(offset: Offset): Offset{
        return Offset(
            x = x / scale - offset.x,
            y = y / scale - offset.y
        )
    }

    fun Size.toViewportSize(): Size{
        return Size(
            width = width / scale,
            height = height / scale,
        )
    }

    fun Size.toWindowSize(): Size{
        return Size(
            width = width * scale,
            height = height * scale,
        )
    }

    @WindowDimension
    fun clippingRect(): Pair<Size, Offset>{
        val windowRatio = windowSize.width / windowSize.height
        val size = if(clippingRectRatio > windowRatio){
            val width = windowSize.width - CLIPPING_RECT_PADDING*2
            val height = width / clippingRectRatio
            Size(width, height)
        }else{
            val height = windowSize.height - CLIPPING_RECT_PADDING*2
            val width = height * clippingRectRatio
            Size(width, height)
        }
        val x = windowSize.width / 2 - size.width / 2
        val y = windowSize.height / 2 - size.height / 2
        val offset = Offset(x,y)
        return size to offset
    }

    companion object{
        const val CLIPPING_RECT_PADDING = 150
    }
}

@Composable
fun rememberViewport(
    windowSize: Size,
): Viewport{
    val viewport = remember {
        Viewport(
            windowSize = windowSize,
            imageSize = windowSize,
        )
    }
    LaunchedEffect(windowSize){
        viewport.windowSize = windowSize
    }
    return viewport
}

private fun Offset.coerceIn(min: Offset, max: Offset): Offset{
    return Offset(x.coerceIn(min.x, max.x), y.coerceIn(min.y, max.y))
}

private operator fun Size.plus(size: Size): Size {
    return Size(width + size.width, height + size.height)
}

private operator fun Offset.plus(size: Size): Offset{
    return Offset(x + size.width, y + size.height)
}

annotation class WindowDimension
annotation class ViewportDimension