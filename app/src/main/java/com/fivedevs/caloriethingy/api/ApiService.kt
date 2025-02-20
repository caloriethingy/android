package com.fivedevs.caloriethingy.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Part

interface ApiService {

    // Register user
    @POST("auth/register")
    suspend fun register(@Body user: User): Response<AuthResponse>

    // Login user
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    // Upload meal image
    @Multipart
    @POST("meal/create-meal")
    suspend fun uploadMeal(@Part file: MultipartBody.Part): Response<MealResponse>

    // Get daily summary
    @GET("meal/get-daily-summary")
    suspend fun getDailySummary(@Header("Authorization") token: String): Response<DailySummaryResponse>
}
