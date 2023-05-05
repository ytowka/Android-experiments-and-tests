package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@Composable
fun ImageCropper(
    image: String
) {
    val imageBitmap by rememberImageBitmap(image)

    imageBitmap?.let {
        Image(it, contentDescription = null)
    }
}