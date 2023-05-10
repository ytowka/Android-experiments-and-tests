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

    val rect: Rect by lazy {
        Rect(offset, size)
    }

    fun circumscribedRect(angle: Float): ClippingRect{
        val rect = with(offset){
            val p1 = rotate(angle)
            val p2 = this.plus(Offset(size.width, 0f)).rotate(angle)
            val p3 = this.plus(Offset(0f, size.height)).rotate(angle)
            val p4 = this.plus(size).rotate(angle)
            Rect(
                Offset(listOf(p1.x, p2.x, p3.x, p4.x).min(),
                listOf(p1.y, p2.y, p3.y, p4.y).min(),),
                Offset(listOf(p1.x, p2.x, p3.x, p4.x).max(),
                    listOf(p1.y, p2.y, p3.y, p4.y).max(),),
            )
        }
        return ClippingRect(rect.size, rect.topLeft);
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