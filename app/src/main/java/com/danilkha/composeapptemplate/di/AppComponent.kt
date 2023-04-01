package com.danilkha.composeapptemplate.di

import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [ViewModelModule::class]
)
@Singleton
interface AppComponent{

    // fun appViewModel(): AppViewModel
}