package com.danilkha.composeapptemplate.view.cropper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.cos
import kotlin.math.roundToInt
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

fun Size.roundToInt() = IntSize(width.roundToInt(), height.roundToInt())

operator fun IntOffset.plus(intSize: IntSize) = IntOffset(x+intSize.width, y + intSize.height)
//operator fun Size.div(f: Float) = Size(width/f, height/f)
fun Offset.roundToInt() = IntOffset(x.roundToInt(), y.roundToInt())

fun Float.toDeg() = this/(2 * Math.PI).toFloat()*360