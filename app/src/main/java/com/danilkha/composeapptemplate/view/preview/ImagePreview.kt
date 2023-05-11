package com.danilkha.composeapptemplate.view.preview

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap

@Composable
fun ImagePreview(
    image: Bitmap
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            modifier = Modifier.align(Alignment.Center),
            bitmap = image.asImageBitmap(),
            contentDescription = null
        )
    }
}