package com.danilkha.composeapptemplate.view.start

data class StartState(val text: String = "hello world")

sealed interface StartEvent

sealed interface StartUserEvent : StartEvent

sealed interface StartSideEffect