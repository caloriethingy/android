package com.fivedevs.caloriethingy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fivedevs.caloriethingy.api.ApiService

class LoginViewModelFactory(private val apiService: ApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(apiService) as T
    }
}