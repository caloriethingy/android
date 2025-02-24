package com.fivedevs.caloriethingy

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.fivedevs.caloriethingy.api.ApiService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), EasyPermissions.PermissionCallbacks {
    private val PREFS_NAME = "app_prefs"
    private val TOKEN_KEY = "auth_token"
    private var photoFile: File? = null
    private val CAMERA_PERMISSION_CODE = 100

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("MainActivity", "takePictureLauncher result: code=${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            Log.d("MainActivity", "Calling uploadPicture()")
            uploadPicture()
        } else {
            Log.w("MainActivity", "Picture capture failed or cancelled: ${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val token = prefs.getString(TOKEN_KEY, null)
        if (token == null) {
            Log.d("MainActivity", "No token found, redirecting to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Check token validity on startup
        lifecycleScope.launch {
            Log.d("MainActivity", "Checking token validity")
            val apiService = ApiClient.getClient().create(ApiService::class.java)
            try {
                val response = apiService.getDailySummary("Bearer $token")
                if (!response.isSuccessful && response.code() == 401) {
                    Log.w("MainActivity", "Token invalid (401), redirecting to LoginActivity")
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .remove(TOKEN_KEY)
                        .commit()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    return@launch
                }
                Log.d("MainActivity", "Token valid, proceeding")
            } catch (e: Exception) {
                Log.e("MainActivity", "Token check failed: ${e.message}", e)
                // Set TOKEN_KEY to null so we don't loop
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .remove(TOKEN_KEY)
                    .commit()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
                return@launch
            }

            // Set Compose content only if token is valid
            setContent {
                MainScreen(
                    onUploadClick = { checkCameraPermission() },
                    onSummaryClick = { startActivity(Intent(this@MainActivity, SummaryActivity::class.java)) } // Fix applied here
                )
            }
        }
    }

    private fun checkCameraPermission() {
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)) {
            takePicture()
        } else {
            EasyPermissions.requestPermissions(
                this, "Camera permission is required",
                CAMERA_PERMISSION_CODE, android.Manifest.permission.CAMERA
            )
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            try {
                photoFile = createImageFile()
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.fivedevs.caloriethingy.fileprovider",
                    photoFile!!
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureLauncher.launch(takePictureIntent)
            } catch (ex: Exception) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun uploadPicture() {
        val photoFile = photoFile ?: return
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val token = prefs.getString(TOKEN_KEY, null)
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val requestFile = photoFile.asRequestBody("image/jpeg".toMediaType())
        val body = MultipartBody.Part.createFormData("MealForm[picture]", photoFile.name, requestFile)
        val authHeader = "Bearer $token"
        val apiService = ApiClient.getClient().create(ApiService::class.java)

        lifecycleScope.launch {
            try {
                val response = apiService.uploadMeal(authHeader, picture = body)
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Upload successful", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("UploadError", "Failed with code ${response.code()}: $errorBody")
                    Toast.makeText(this@MainActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        takePicture()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(this, "Camera permission denied permanently", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MainScreen(onUploadClick: () -> Unit, onSummaryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onUploadClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Meal Picture")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSummaryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Daily Summary")
        }
    }
}