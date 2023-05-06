package com.danilkha.composeapptemplate.view.cropper

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.max

@Stable
class Viewport(
    imageSize: Size,

    offset: Offset = Offset(0f,0f),
    clippingRect: ClippingRect = ClippingRect(Size.Zero, Offset.Zero),
    scale: Float = 1f,
) {

    @WindowDimension var clippingRect: ClippingRect by mutableStateOf(clippingRect, structuralEqualityPolicy())

    @ViewportDimension var imageSize: Size by mutableStateOf(imageSize, structuralEqualityPolicy())

    @ViewportDimension var offset: Offset by mutableStateOf(offset, structuralEqualityPolicy())


    var scale: Float by mutableStateOf(scale)

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
            val clippingRectSize = clippingRect.size;
            return max(
                clippingRectSize.width / imageSize.width,
                clippingRectSize.height / imageSize.height,
            )
        }

    val maxScale: Float = 2.5f

    fun Offset.rectLimited(): Offset{
        var (clipSize, clipOffset) = clippingRect;
        clipSize = clipSize.toViewportSize()
        clipOffset = clipOffset.toLocalOffset(this)

        var newX = x
        var newY = y

        val rightOverDrag = clipOffset.x + clipSize.width - imageSize.width
        if(clipOffset.x < 0) {
            newX = x + clipOffset.x
        }else if(rightOverDrag > 0){
            newX = x + rightOverDrag
        }

        val bottomOverDrag = clipOffset.y + clipSize.height - imageSize.height
        if(clipOffset.y < 0){
            newY = y + clipOffset.y
        }else if(bottomOverDrag > 0){
            newY = y + bottomOverDrag
        }

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
}

@Composable
fun rememberViewport(): Viewport{
    val viewport = remember { Viewport(imageSize = Size.Zero,) }
    return viewport
}
operator fun Size.plus(size: Size): Size {
    return Size(width + size.width, height + size.height)
}

operator fun Offset.plus(size: Size): Offset{
    return Offset(x + size.width, y + size.height)
}

operator fun Offset.minus(size: Size): Offset{
    return Offset(x - size.width, y - size.height)
}

annotation class WindowDimension
annotation class ViewportDimension