package com.danilkha.composeapptemplate.view.start

import com.danilkha.composeapptemplate.utils.MviViewModel
import javax.inject.Inject

class StartViewModel @Inject constructor(): MviViewModel<StartState, StartEvent, StartUserEvent, StartSideEffect>(){
    override val startState: StartState = StartState();

    override fun reduce(currentState: StartState, event: StartEvent): StartState {
        return when(event){
            else -> { currentState }
        }
    }
}