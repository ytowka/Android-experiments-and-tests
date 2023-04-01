package com.danilkha.composeapptemplate.view.start

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.danilkha.composeapptemplate.utils.currentViewModel

@Composable
fun StartScreen(
    viewModel: StartViewModel = currentViewModel { it.startViewModel() }
) {
    val state: StartState by viewModel.state.collectAsState()
    Text(text = state.text)
}