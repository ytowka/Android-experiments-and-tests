package com.danilkha.composeapptemplate.di

import com.danilkha.composeapptemplate.view.start.StartViewModel
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [ViewModelModule::class]
)
@Singleton
interface AppComponent{

    fun startViewModel(): StartViewModel
}