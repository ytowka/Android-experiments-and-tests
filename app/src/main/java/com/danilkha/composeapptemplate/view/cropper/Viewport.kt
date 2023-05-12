package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Stable
class Viewport(
    imageSize: Size,

    offset: Offset = Offset(0f,0f),
    clippingRect: ClippingRect = ClippingRect(Size.Zero, Offset.Zero),
    scale: Float = 1f,
) {

    @WindowDimension var clippingRect: ClippingRect by mutableStateOf(clippingRect, structuralEqualityPolicy())

    @ViewportDimension var imageSize: Size by mutableStateOf(imageSize, structuralEqualityPolicy())

    @ViewportDimension var center: Offset by mutableStateOf(offset, structuralEqualityPolicy())

    @WindowDimension val imageTopLeft
        get() = Offset(
            -imageSize.width/2,
            -imageSize.height/2
        )

    var scale: Float by mutableStateOf(scale)

    var angle: Float by mutableStateOf(0f)

    fun zoom(scale: Float, @WindowDimension anchor: Offset){
        val viewportAnchor = anchor.toViewportOffset()
        this.scale = (this.scale * scale).coerceIn(minScale, maxScale)
        val newViewportAnchor = anchor.toViewportOffset()
        center = (center - (viewportAnchor - newViewportAnchor)).rectLimited()
    }

    fun rotate(angle: Float){
        this.angle += angle
        this.scale = (this.scale).coerceIn(minScale, maxScale)
        center = center.rectLimited()
    }

    fun translate(@WindowDimension delta: Offset){
        center = (center + delta.rotate(-angle) / scale).rectLimited()
    }

    val minScale: Float
        get(){
            val clippingRectSize = clippingRect.circumscribedRect(-angle).size;
            return max(
                clippingRectSize.width / imageSize.width,
                clippingRectSize.height / imageSize.height,
            )
        }

    val maxScale: Float = 2.5f

    fun Offset.rectLimited(): Offset{
        var (clipSize, clipOffset) = clippingRect.toLocal(this).circumscribedRect(-angle);

        var newX = x
        var newY = y

        val rightOverDrag = clipOffset.x + clipSize.width - imageSize.width/2
        val leftOverDrag =  imageSize.width/2 + clipOffset.x
        if(leftOverDrag < 0) {
            newX = x + leftOverDrag
        }else if(rightOverDrag > 0){
            newX = x + rightOverDrag
        }

        val bottomOverDrag = clipOffset.y + clipSize.height - imageSize.height/2
        val topOverDrag = imageSize.height/2 + clipOffset.y
        if(topOverDrag< 0){
            newY = y + topOverDrag
        }else if(bottomOverDrag > 0){
            newY = y + bottomOverDrag
        }

        return Offset(newX, newY)
    }


    fun Offset.toViewportOffset(): Offset{
        return /*rotate(-angle)*/this.run {
            Offset(
                x = x / scale - center.x,
                y = y / scale - center.y,
            )
        }
    }

    fun Offset.toWindowOffset(): Offset{
        return Offset(
                x = (x + center.x) * scale,
                y = (y + center.y) * scale,
            )/*.rotate(angle)*/
    }

    fun Offset.toLocalOffset(@WindowDimension offset: Offset): Offset{
        return /*rotate(-angle)*/this.run {
            Offset(
                x = x / scale - offset.x,
                y = y / scale - offset.y,
            )
        }

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

    fun ClippingRect.toLocal(newOffset: Offset): ClippingRect = ClippingRect(size.toViewportSize(), offset.toLocalOffset(newOffset))
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

fun Offset.rotate(/*rad*/ angle: Float, pivot: Offset = Offset.Zero): Offset = (this - pivot).run {
    Offset(
        x = x * cos(angle) - y * sin(angle),
        y = x * sin(angle) + y * cos(angle),
    )
} + pivot
annotation class WindowDimension
annotation class ViewportDimension