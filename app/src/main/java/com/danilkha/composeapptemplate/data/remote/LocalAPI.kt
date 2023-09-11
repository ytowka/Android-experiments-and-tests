package com.danilkha.composeapptemplate.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface LocalAPI {

    @GET("/m/")
    suspend fun getGreeting(): DataResponse
}