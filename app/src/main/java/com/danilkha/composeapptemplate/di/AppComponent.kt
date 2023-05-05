package com.danilkha.composeapptemplate.di

import android.content.Context
import com.danilkha.composeapptemplate.entrypoints.App
import com.danilkha.composeapptemplate.view.start.StartViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

@Component(
    modules = [ViewModelModule::class]
)
interface AppComponent{

    fun inject(app: App)
    fun startViewModel(): StartViewModel



    @Component.Builder
    interface Builder{
        @BindsInstance
        fun app(app: App): Builder

        fun build(): AppComponent
    }
}