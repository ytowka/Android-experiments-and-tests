package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

data class ClippingRect(val size: Size, val offset: Offset) {

    val rectCentered: Rect by lazy {
        Rect(
            -size.width/2,
             -size.height/2,
             size.width/2,
            size.height/2
        )
    }

    val rect: Rect by lazy {
        Rect(offset, size)
    }

    fun circumscribedRect(angle: Float): ClippingRect{
        val newSize = with(size){
            val newWidth = width * cos(angle).absoluteValue + height* sin(angle).absoluteValue
            val newHeight = height * cos(angle).absoluteValue + width* sin(angle).absoluteValue
            Size(newWidth, newHeight)
        }
        val newOffset = with(Offset.Zero - size/2f){
            val topLeft = rotate(-angle)
            val topRight = this.plus(Offset(size.width, 0f)).rotate(-angle)
            val bottomLeft = this.plus(Offset(0f, size.height)).rotate(-angle)
            val bottomRight = this.plus(size).rotate(-angle)
            Offset(
                listOf(topLeft.x, topRight.x, bottomRight.x, bottomLeft.x).min(),
                listOf(topLeft.y, topRight.y, bottomRight.y, bottomLeft.y).min(),
            )
        }
        return ClippingRect(newSize, offset+newOffset+size/2f);
    }
    companion object{
        fun clippingRect(windowSize: Size, ratio: Float): ClippingRect{
            val windowRatio = windowSize.width / windowSize.height
            val size = if(ratio > windowRatio){
                val width = windowSize.width - CLIPPING_RECT_PADDING * 2
                val height = width / ratio
                Size(width, height)
            }else{
                val height = windowSize.height - CLIPPING_RECT_PADDING * 2
                val width = height * ratio
                Size(width, height)
            }
            val x = windowSize.width / 2 - size.width / 2
            val y = windowSize.height / 2 - size.height / 2
            val offset = Offset(x,y)
            return ClippingRect(size, offset)
        }

        const val CLIPPING_RECT_PADDING = 250
    }
}