package com.fivedevs.caloriethingy.api.models

data class DailySummaryResponse(val totalCalories: Int, val meals: List<MealSummary>)

data class MealSummary(val food_name: String, val calories: Int)
