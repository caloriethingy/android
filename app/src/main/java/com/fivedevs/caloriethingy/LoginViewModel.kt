package com.fivedevs.caloriethingy

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fivedevs.caloriethingy.api.ApiService
import com.fivedevs.caloriethingy.api.models.LoginRequest
import com.fivedevs.caloriethingy.api.models.AuthResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    val loggedIn = mutableStateOf(false)

    // Function to handle login
    fun login() {
        isLoading.value = true
        loggedIn.value = false
        errorMessage.value = ""

        val loginRequest = LoginRequest(email.value, password.value)
        viewModelScope.launch {
            try {
                val response: Response<AuthResponse> = apiService.login(loginRequest)
                if (response.isSuccessful) {
                    // Handle successful login
                    val authResponse = response.body()
                    loggedIn.value = true
                    authResponse?.let {
                        // Save token or handle as needed
                    }
                } else {
                    // Handle API error
                    errorMessage.value = "Login failed: ${response.message()}"
                }
            } catch (e: Exception) {
                // Handle network error
                errorMessage.value = "Network error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
