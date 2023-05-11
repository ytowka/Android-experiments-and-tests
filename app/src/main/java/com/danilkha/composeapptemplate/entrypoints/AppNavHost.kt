package com.danilkha.composeapptemplate.entrypoints

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danilkha.composeapptemplate.view.cropper.ImageCropper
import com.danilkha.composeapptemplate.view.preview.ImagePreview
import com.danilkha.composeapptemplate.view.start.StartScreen
import kotlin.math.roundToInt

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    var image by remember { mutableStateOf<Bitmap?>(null) }

    NavHost(navController, "start"){
        composable(
            route = "cropper/{image}",
        ){
            ImageCropper(
                image = Uri.decode(it.arguments?.getString("image")) ?: "",
                onImageSave = { bm, vp ->
                    bm?.let {
                        with(vp){
                            val bitmap = Bitmap.createBitmap(
                                it,
                                clippingRect.offset.toViewportOffset().x.roundToInt(),
                                clippingRect.offset.toViewportOffset().y.roundToInt(),
                                clippingRect.size.toViewportSize().width.roundToInt(),
                                clippingRect.size.toViewportSize().height.roundToInt(),
                            )
                            image = bitmap
                            navController.navigate("preview")
                        }
                    }
                }
            )
        }
        composable("preview"){
            image?.let {
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