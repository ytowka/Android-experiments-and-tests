package com.danilkha.composeapptemplate.view.cropper

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap

data class SizedImageBitmap(val imageBitmap: ImageBitmap, val size: Size, val bitmap: Bitmap){

}