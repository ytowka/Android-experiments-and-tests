package com.danilkha.composeapptemplate.view.start

data class StartState(
    val images: List<String> = emptyList(),
)

sealed interface StartEvent{
    data class ImagesGot(val images: List<String>) : StartEvent
}

sealed interface StartUserEvent : StartEvent{
    data class CameraPermissionGranted(val granted: Boolean) : StartUserEvent
    data class StoragePermissionGranted(val granted: Boolean) : StartUserEvent
    data class ImageTaken(val uri: String) : StartUserEvent

    object GetAllImages : StartUserEvent
}

sealed interface StartSideEffect{

}