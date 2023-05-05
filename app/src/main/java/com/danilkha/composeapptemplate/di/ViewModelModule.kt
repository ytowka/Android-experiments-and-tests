package com.danilkha.composeapptemplate.di

import android.content.Context
import com.danilkha.composeapptemplate.entrypoints.App
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {
    @Provides
    fun context(app: App): Context {
        return app
    }
}