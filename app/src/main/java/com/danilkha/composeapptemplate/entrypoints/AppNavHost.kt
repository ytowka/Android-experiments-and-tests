package com.danilkha.composeapptemplate.entrypoints

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danilkha.composeapptemplate.view.cropper.ImageCropper
import com.danilkha.composeapptemplate.view.cropper.asOffset
import com.danilkha.composeapptemplate.view.cropper.minus
import com.danilkha.composeapptemplate.view.cropper.plus
import com.danilkha.composeapptemplate.view.cropper.rememberImageBitmap
import com.danilkha.composeapptemplate.view.cropper.rotate
import com.danilkha.composeapptemplate.view.cropper.roundToInt
import com.danilkha.composeapptemplate.view.cropper.toRad
import com.danilkha.composeapptemplate.view.preview.ImagePreview
import com.danilkha.composeapptemplate.view.start.StartScreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    NavHost(navController, "start"){
        composable(
            route = "cropper/{image}",
        ){
            val image = Uri.decode(it.arguments?.getString("image")) ?: ""
            val imageBitmap by rememberImageBitmap(image)
            imageBitmap?.let {
                ImageCropper(
                    bitmap = it,
                    onImageSave = { rect, angle ->
                        bitmap = crop(rect.topLeft, rect.size, angle, it)
                        navController.navigate("preview")
                    }
                )
            }
        }
        composable("preview"){
            bitmap?.let {
                ImagePreview(it)
            }
        }
        composable("start"){
            StartScreen(onImageClicked = {
                navController.navigate("cropper/${Uri.encode(it)}")
            })
        }
    }
}
fun crop(offset: Offset, size: Size, angle: Float, bitmap: Bitmap): Bitmap{
    val matrix = Matrix().apply {
        postRotate(angle)
    };
    val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    val intOffset = (offset).roundToInt()
    val intSize = size.roundToInt()

    val croppedBitmap = Bitmap.createBitmap(
        rotated,
        intOffset.x,
        intOffset.y,
        intSize.width,
        intSize.height,
    )

    return croppedBitmap
}