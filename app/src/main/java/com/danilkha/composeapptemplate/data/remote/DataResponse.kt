package com.danilkha.composeapptemplate.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class DataResponse(
    val name: String,
    val date: Long,
)
