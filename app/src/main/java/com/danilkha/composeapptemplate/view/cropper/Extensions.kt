package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin

operator fun Size.plus(size: Size): Size {
    return Size(width + size.width, height + size.height)
}

operator fun Offset.plus(size: Size): Offset {
    return Offset(x + size.width, y + size.height)
}

operator fun Offset.minus(size: Size): Offset {
    return Offset(x - size.width, y - size.height)
}

fun Offset.rotate(/*rad*/ angle: Float, pivot: Offset = Offset.Zero): Offset = (this - pivot).run {
    Offset(
        x = x * cos(angle) - y * sin(angle),
        y = x * sin(angle) + y * cos(angle),
    )
} + pivot