package com.danilkha.composeapptemplate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    color: Color = MaterialTheme.colors.primary,
    style: TextStyle = TextStyle.Default.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
){
    Text(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(8.dp)
        ,
        text = text,
        color = color,
        style = style
    )
}