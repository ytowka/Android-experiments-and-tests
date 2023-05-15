package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Stable
class Viewport(
    imageSize: Size,

    offset: Offset = Offset.Zero,
    clippingRect: ClippingRect = ClippingRect(Size.Zero, Offset.Zero),
    scale: Float = 1f,
    val maxScaleCoef: Float = 3f
) {

    @WindowDimension var clippingRect: ClippingRect by mutableStateOf(clippingRect, structuralEqualityPolicy())

    @ViewportDimension var imageSize: Size by mutableStateOf(imageSize, structuralEqualityPolicy())

    @ViewportDimension var center: Offset by mutableStateOf(offset, structuralEqualityPolicy())

    private var lastScale: Float? = null

    @WindowDimension val imageTopLeft
        get() = Offset(
            -imageSize.width/2,
            -imageSize.height/2
        )

    @WindowDimension val rotatedImageTopLeft: Offset
        get() {
            return clippingRect.rect.center.toViewportOffset().let{
                (-it).rotate(angle) + it - ClippingRect(imageSize, Offset.Zero).circumscribedRect(angle).size/2f
            }
        }
    fun getCropArea(): Rect {
        val offset = clippingRect.offset.toViewportOffset() - rotatedImageTopLeft
        val size = clippingRect.size.toViewportSize();

        return Rect(offset, size)
    }

    @WindowDimension val clipOffset: Offset
        get() {
            val viewportRotatedTopLeft = clippingRect.rect.center.toViewportOffset().let{
                (-it).rotate(angle) + it - ClippingRect(imageSize, Offset.Zero).circumscribedRect(angle).size/2f
            }

            val clippingRectTopLeft = clippingRect.offset.toViewportOffset()
            return clippingRectTopLeft - viewportRotatedTopLeft
        }

    var scale: Float by mutableStateOf(scale)

    var angle: Float by mutableStateOf(0f)

    fun zoom(scale: Float, @WindowDimension anchor: Offset){
        val viewportAnchor = anchor.toViewportOffset()
        val minScale = minScale
        this.scale = (this.scale * scale).coerceIn(minScale, maxScale)
        val newViewportAnchor = anchor.toViewportOffset()
        lastScale = null
        center = (center - (viewportAnchor - newViewportAnchor)).rectLimited()
    }


    fun rotate(angle: Float){
        val clippingRectCenter = clippingRect.rect.center
        val viewportAnchor = clippingRectCenter.toViewportOffset()
        this.angle = (this.angle + angle) % (2 * Math.PI).toFloat()
        val lastScale = this.lastScale
        val newScale = if(lastScale != null){
            lastScale
        }else{
            this.lastScale = scale
            scale
        }.coerceIn(minScale, maxScale)
        this.scale = newScale
        val newViewportAnchor = clippingRectCenter.toViewportOffset()
        center = (center - (viewportAnchor - newViewportAnchor)).rectLimited()
    }

    fun setRotate(angle: Float){
        this.angle = angle % (2 * Math.PI).toFloat()
        this.scale = minScale
        center = center.rectLimited()
    }

    fun translate(@WindowDimension delta: Offset){
        center = (center + delta.rotate(-angle) / scale).rectLimited()
        lastScale = null
    }

    fun bringToCenter(){
        angle = 0f
        scale = minScale
        lastScale = null
        center = clippingRect.toLocal(Offset.Zero).run { offset + size/2f }.rectLimited()
    }

    val minScale: Float
        get(){
            val clippingRectSize = clippingRect.circumscribedRect(-angle).size;
            return max(
                clippingRectSize.width / imageSize.width,
                clippingRectSize.height / imageSize.height,
            )
        }

    val maxScale: Float
        get() {
            val clippingRectSize = clippingRect.size
            return max(
                clippingRectSize.width / imageSize.width,
                clippingRectSize.height / imageSize.height,
            ) * maxScaleCoef
        }

    @ViewportDimension
    fun Offset.rectLimited(): Offset{
        val (clipSize, clipOffset) = clippingRect.toLocal(this).circumscribedRect(-angle);

        val rightOverDrag = clipOffset.x + clipSize.width - imageSize.width/2
        val leftOverDrag =  imageSize.width/2 + clipOffset.x

        val bottomOverDrag = clipOffset.y + clipSize.height - imageSize.height/2
        val topOverDrag = imageSize.height/2 + clipOffset.y

        return Offset(
            x.coerceAtLeast( x + rightOverDrag).coerceAtMost(x + leftOverDrag),
            y.coerceAtLeast(y + bottomOverDrag).coerceAtMost(y + topOverDrag)
        )
    }


    fun Offset.toViewportOffset(): Offset{
        return Offset(
            x = x / scale - center.x,
            y = y / scale - center.y,
        )
    }

    fun Offset.toWindowOffset(): Offset{
        return Offset(
                x = (x + center.x) * scale,
                y = (y + center.y) * scale,
            )
    }

    fun Offset.toLocalOffset(@WindowDimension offset: Offset): Offset{
        return Offset(
            x = x / scale - offset.x,
            y = y / scale - offset.y,
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

    fun ClippingRect.toLocal(newOffset: Offset): ClippingRect = ClippingRect(size.toViewportSize(), offset.toLocalOffset(newOffset))
}

@Composable
fun rememberViewport(clippingRect: ClippingRect, imageSize: Size): Viewport {
    val viewport = remember {
        Viewport(
            imageSize = imageSize,
            clippingRect = clippingRect
        )
    }
    LaunchedEffect(Unit){
        viewport.bringToCenter()
    }

    LaunchedEffect(imageSize){
        viewport.imageSize = imageSize;
    }

    LaunchedEffect(clippingRect){
        viewport.clippingRect = clippingRect;
    }
    return viewport
}

annotation class WindowDimension
annotation class ViewportDimension