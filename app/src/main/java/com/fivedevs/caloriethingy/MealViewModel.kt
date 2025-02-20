package com.fivedevs.caloriethingy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

class MealViewModel : ViewModel() {

    private val repository = MealRepository(ApiClient.apiService)

    // LiveData to observe the API responses
    private val _mealResponse = MutableLiveData<MealResponse>()
    val mealResponse: LiveData<MealResponse> get() = _mealResponse

    private val _dailySummary = MutableLiveData<DailySummaryResponse>()
    val dailySummary: LiveData<DailySummaryResponse> get() = _dailySummary

    // Function to upload a meal image
    fun uploadMealImage(file: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = repository.uploadMealImage(file)
                if (response.isSuccessful) {
                    _mealResponse.value = response.body()
                } else {
                    // Handle error case
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    // Function to get daily summary
    fun getDailySummary(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getDailySummary(token)
                if (response.isSuccessful) {
                    _dailySummary.value = response.body()
                } else {
                    // Handle error case
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
