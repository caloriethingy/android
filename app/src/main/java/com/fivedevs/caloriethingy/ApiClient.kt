package com.fivedevs.caloriethingy.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://localhost:20081/"

    // Create OkHttpClient to handle logging (optional but useful for debugging)
    private val client = OkHttpClient.Builder().build()

    // Create Retrofit instance
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
