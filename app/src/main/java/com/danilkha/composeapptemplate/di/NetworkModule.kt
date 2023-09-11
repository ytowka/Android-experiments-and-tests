package com.danilkha.composeapptemplate.di

import com.danilkha.composeapptemplate.data.remote.LocalAPI
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit{
        val contentType = "application/json".toMediaType()

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.17.8.200:8080/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): LocalAPI{
        return retrofit.create(LocalAPI::class.java)
    }
}