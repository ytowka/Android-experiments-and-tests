package com.danilkha.composeapptemplate.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.danilkha.composeapptemplate.di.AppComponent
import com.danilkha.composeapptemplate.entrypoints.App

val Activity.app: App
    get() = this.application as App

fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

val appComponent: AppComponent
    @Composable
    get() = LocalContext.current.findActivity().app.appComponent

val Context.injector: AppComponent
    get() = (this.applicationContext as App).appComponent

