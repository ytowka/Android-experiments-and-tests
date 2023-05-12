package com.danilkha.composeapptemplate.entrypoints

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danilkha.composeapptemplate.view.cropper.ImageCropper
import com.danilkha.composeapptemplate.view.cropper.rememberImageBitmap
import com.danilkha.composeapptemplate.view.cropper.roundToInt
import com.danilkha.composeapptemplate.view.preview.ImagePreview
import com.danilkha.composeapptemplate.view.start.StartScreen
import kotlin.math.roundToInt

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
                    onImageSave = { offset, size, angle ->
                        bitmap = crop(offset.roundToInt(), size.roundToInt(), angle, it)
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
fun crop(offset: IntOffset, size: IntSize, angle: Float, bitmap: Bitmap): Bitmap{
    val matrix = Matrix();
    matrix.postRotate(angle)
    val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    val croppedBitmap = Bitmap.createBitmap(
        rotated,
        offset.x,
        offset.y,
        size.width,
        size.height,
    )

    return croppedBitmap
}