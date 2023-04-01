package com.danilkha.composeapptemplate.entrypoints

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danilkha.composeapptemplate.view.start.StartScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController, "start"){
        composable("start"){
            StartScreen()
        }
    }
}