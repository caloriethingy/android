package com.fivedevs.caloriethingy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MainActivity : ComponentActivity() {

    private val mealViewModel: MealViewModel by viewModels()
    // Initialize the LoginViewModel
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(RetrofitInstance.apiService)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(onNavigateBack = { /* Handle back navigation */ }, loginViewModel)
        }

        // Upload meal image example (replace with actual file logic)
        val file = File("path/to/your/image.jpg")  // Replace with your file path
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("picture", file.name, requestBody)

        // Call the ViewModel function to upload image
        mealViewModel.uploadMealImage(part)

        // Observe the meal response
        mealViewModel.mealResponse.observe(this) { response ->
            // Handle success case (update UI, etc.)
        }

        // To test daily summary (replace "your_token" with an actual JWT token)
        mealViewModel.getDailySummary("your_token")
    }

    @Composable
    fun GuestScreen() {
        var isLoginScreen by remember { mutableStateOf(false) }
        var isRegisterScreen by remember { mutableStateOf(false) }

        // Conditional display based on the screen choice
        if (isLoginScreen) {
            LoginScreen(onNavigateBack = { /* Handle back navigation */ }, loginViewModel)
        } else if (isRegisterScreen) {
            RegisterScreen(onNavigateBack = { isRegisterScreen = false })
        } else {
            // Default Guest Screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Calorie Thingy!", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Oh hi \uD83D\uDC4B! I don't know what I'm doing - this probably works.")

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = { isRegisterScreen = true }) {
                    Text("Register")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = { isLoginScreen = true }) {
                    Text("Login")
                }
            }
        }
    }
}


@Preview
@Composable
fun RegisterScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register Screen")

        // You can add your register form here (email, password, etc.)

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { onNavigateBack() }) {
            Text("Back to Guest Screen")
        }
    }
}
