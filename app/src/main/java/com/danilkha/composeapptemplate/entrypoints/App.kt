package com.danilkha.composeapptemplate.entrypoints

import android.app.Application
import com.danilkha.composeapptemplate.di.AppComponent
import com.danilkha.composeapptemplate.di.DaggerAppComponent
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class App : Application(){

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .app(this)
            .build()
    }

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
    }
}