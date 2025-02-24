package com.fivedevs.caloriethingy

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory

object ApiClient {
    private const val BASE_URL = "https://shipwise.ngrok.io/"
    private var retrofit: Retrofit? = null

    // Create Retrofit instance
    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }
        return retrofit!!
    }
}
