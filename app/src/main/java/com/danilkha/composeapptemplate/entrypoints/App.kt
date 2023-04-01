package com.danilkha.composeapptemplate.entrypoints

import android.app.Application
import com.danilkha.composeapptemplate.di.AppComponent
import com.danilkha.composeapptemplate.di.DaggerAppComponent

class App : Application() {

    val appComponent: AppComponent by lazy { DaggerAppComponent.create() }

    override fun onCreate() {
        super.onCreate()
    }
}