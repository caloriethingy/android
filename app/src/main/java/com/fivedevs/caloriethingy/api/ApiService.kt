package com.fivedevs.caloriethingy.api

import com.fivedevs.caloriethingy.api.models.AuthResponse
import com.fivedevs.caloriethingy.api.models.CreateMealResponse
import com.fivedevs.caloriethingy.api.models.SummaryResponse
import com.fivedevs.caloriethingy.api.models.User
import com.fivedevs.caloriethingy.api.models.UserRegister
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body body: UserRegister): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: User): Response<AuthResponse>

    @Multipart
    @POST("meal/create-meal")
    suspend fun uploadMeal(
        @Header("Authorization") authHeader: String,
        @Header("Accept") accept: String = "application/json",
        @Part picture: MultipartBody.Part
    ): Response<CreateMealResponse>

    @GET("meals/get-daily-summary")
    suspend fun getDailySummary(@Header("Authorization") authHeader: String): Response<SummaryResponse>

}