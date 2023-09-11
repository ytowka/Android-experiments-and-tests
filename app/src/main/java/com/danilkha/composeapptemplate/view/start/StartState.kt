package com.danilkha.composeapptemplate.view.start

import java.util.Date

data class StartState(val text: String = "hello world", val date: Date = Date())

sealed interface StartEvent{

    class GotGreeting(
        val text: String,
        val date: Date
    ): StartEvent
}

sealed interface StartUserEvent : StartEvent

sealed interface StartSideEffect