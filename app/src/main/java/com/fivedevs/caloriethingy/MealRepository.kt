package com.fivedevs.caloriethingy

import okhttp3.MultipartBody
import retrofit2.Response

class MealRepository(private val apiService: ApiService) {

    // Function to upload meal image
    suspend fun uploadMealImage(file: MultipartBody.Part): Response<MealResponse> {
        return apiService.uploadMeal(file)
    }

    // Function to get daily summary (replace with actual endpoint)
    suspend fun getDailySummary(token: String): Response<DailySummaryResponse> {
        return apiService.getDailySummary("Bearer $token")
    }
}
