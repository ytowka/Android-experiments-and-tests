package com.danilkha.composeapptemplate.entrypoints

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import com.danilkha.composeapptemplate.view.cropper.ImageCropper
import com.danilkha.composeapptemplate.view.start.StartScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController, "start"){
        composable(
            route = "cropper/{image}",
        ){
            ImageCropper(Uri.decode(it.arguments?.getString("image")) ?: "")
        }
        composable("start"){
            StartScreen(onImageClicked = {
                navController.navigate("cropper/${Uri.encode(it)}")
            })
        }
    }
}