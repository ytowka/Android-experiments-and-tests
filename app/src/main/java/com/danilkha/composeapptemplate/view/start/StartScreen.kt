package com.danilkha.composeapptemplate.view.start

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.danilkha.composeapptemplate.utils.viewModel
import java.text.SimpleDateFormat

@Composable
fun StartScreen(
    viewModel: StartViewModel = viewModel { it.startViewModel() }
) {
    val state: StartState by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text(text = state.text)
        Text(text = SimpleDateFormat("dd mm yyyy").format(state.date))
    }
}