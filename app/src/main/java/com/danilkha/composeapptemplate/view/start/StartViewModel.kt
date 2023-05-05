package com.danilkha.composeapptemplate.view.start

import com.danilkha.composeapptemplate.data.ImageRepository
import com.danilkha.composeapptemplate.utils.MviViewModel
import javax.inject.Inject

class StartViewModel @Inject constructor(
    val imageRepository: ImageRepository
): MviViewModel<StartState, StartEvent, StartUserEvent, StartSideEffect>(){
    override val startState: StartState = StartState();

    override fun reduce(currentState: StartState, event: StartEvent): StartState {
        return when(event){
            is StartEvent.ImagesGot ->{
                currentState.copy(images = event.images)
            }
            else -> {
                currentState
            }
        }
    }

    override suspend fun onSideEffect(
        prevState: StartState,
        newState: StartState,
        event: StartEvent
    ) {
        when(event){
            StartUserEvent.GetAllImages -> {
                processEvent(StartEvent.ImagesGot(imageRepository.getAllImages()))
            }
            else -> {}
        }
    }
}