package com.danilkha.composeapptemplate.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.danilkha.composeapptemplate.di.AppComponent


@Composable
inline fun<reified T : ViewModel> getCurrentViewModel(crossinline getInstance: () -> T) : T {
    return ViewModelProvider(
        owner = LocalViewModelStoreOwner.current!!,
        factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return getInstance() as T
            }
        }
    )[T::class.java]
}

@Composable
inline fun<reified T : ViewModel> currentViewModel(crossinline getInstance: (appComponent: AppComponent) -> T) : T {
    val appComponent = appComponent
    return ViewModelProvider(
        owner = LocalViewModelStoreOwner.current!!,
        factory = object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return getInstance(appComponent) as T
            }
        }
    )[T::class.java]
}

@Composable
inline fun<reified T : ViewModel> activityViewModel(crossinline getInstance: () -> T) : T {
    return ViewModelProvider(LocalContext.current.findActivity(), factory = object : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return getInstance() as T
        }
    })[T::class.java]
}

inline fun<reified T: ViewModel> ViewModelStoreOwner.viewModel(crossinline getInstance: () -> T): Lazy<T> = lazy {
    ViewModelProvider(this, factory = object : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return getInstance() as T
        }
    })[T::class.java]
}
