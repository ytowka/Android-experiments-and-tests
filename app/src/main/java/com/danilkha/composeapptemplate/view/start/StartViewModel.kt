package com.danilkha.composeapptemplate.view.start

import androidx.lifecycle.viewModelScope
import com.danilkha.composeapptemplate.data.remote.LocalAPI
import com.danilkha.composeapptemplate.utils.MviViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class StartViewModel @Inject constructor(
    val localAPI: LocalAPI
): MviViewModel<StartState, StartEvent, StartUserEvent, StartSideEffect>(){
    override val startState: StartState = StartState();

    override fun reduce(currentState: StartState, event: StartEvent): StartState {
        return when(event){
            is StartEvent.GotGreeting ->{
               currentState.copy(text = event.text, date = event.date)
            }
            else -> { currentState }
        }
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            val response = localAPI.getGreeting()
            val date = Date(response.date)
            processEvent(StartEvent.GotGreeting(
                response.name,
                date
            ))
        }



    }
}