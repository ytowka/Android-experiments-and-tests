package com.danilkha.composeapptemplate.view.cropper

import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision

@Composable
fun rememberImageBitmap(uri: String): State<ImageBitmap?> {
    var rememberedBitmap = remember(uri){ mutableStateOf<ImageBitmap?>(null) }

    val request = ImageRequest.Builder(LocalContext.current)
        .data(uri)
        .precision(Precision.INEXACT)
        .build()
    val ctx = LocalContext.current.applicationContext

    LaunchedEffect(uri) {
        when(val loaderResult = ctx.imageLoader.execute(request = request)) {
            is ErrorResult -> {
                Log.wtf("Image load", "onError: LOL WTF?")
            }
            is SuccessResult -> {
                val bitmapDrawable = (loaderResult.drawable as BitmapDrawable)
                rememberedBitmap.value = bitmapDrawable.bitmap.asImageBitmap()

                val size = with(bitmapDrawable) {
                    Size(
                        width = bitmap.width.toFloat(),
                        height = bitmap.height.toFloat(),
                    )
                }
                /*bitmapDrawableSize = size
                storedSize = st.value?.imageToCrop?.size
                dataSource = loaderResult.dataSource
                isSampled = loaderResult.isSampled*/
            }
        }
    }
    return rememberedBitmap
}