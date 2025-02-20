package com.fivedevs.caloriethingy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import okhttp3.MultipartBody
import okio.ByteString
import java.io.File

class MainActivity : ComponentActivity() {

    private val mealViewModel: MealViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Here you would setup your Composables
            DisplayMealData()
        }

        // Upload meal image example (replace with actual file logic)
        val file = File("path/to/your/image.jpg")  // Replace with your file path
        val requestBody = MultipartBody.create(
            okhttp3.MediaType.parse("image/jpeg"),
            file
        )
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
    fun DisplayMealData() {
        Text("Meal uploaded successfully!")  // Update this based on actual response
    }
}
