package com.fivedevs.caloriethingy.api.models

data class SummaryResponse(
    val calories: Int?,
    val protein: Int?,
    val fat: Int?,
    val carbohydrates: Int?,
    val fiber: Int?
)

