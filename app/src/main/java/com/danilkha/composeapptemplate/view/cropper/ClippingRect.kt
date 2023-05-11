package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class ClippingRect(val size: Size, val offset: Offset) {
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

        const val CLIPPING_RECT_PADDING = 100
    }
}