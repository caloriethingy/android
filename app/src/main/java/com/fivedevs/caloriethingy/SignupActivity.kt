package com.fivedevs.caloriethingy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.fivedevs.caloriethingy.api.ApiService
import com.fivedevs.caloriethingy.api.models.User
import com.fivedevs.caloriethingy.api.models.UserRegister
import kotlinx.coroutines.launch

class SignupActivity : ComponentActivity() {
    private val PREFS_NAME = "app_prefs"
    private val TOKEN_KEY = "auth_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignupScreen(
                onSignupClick = { firstName, email, password -> handleSignup(firstName, email, password) },
                onLoginClick = { startActivity(Intent(this, LoginActivity::class.java)) }
            )
        }
    }

    private fun handleSignup(firstName: String, email: String, password: String) {
        lifecycleScope.launch {
            val apiService = ApiClient.getClient().create(ApiService::class.java)
            try {
                val response = apiService.register(UserRegister(firstName, email, password))
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(TOKEN_KEY, token)
                            .apply()
                        startActivity(Intent(this@SignupActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignupActivity, "Signup failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignupActivity, "Signup failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignupActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun SignupScreen(onSignupClick: (String, String, String) -> Unit, onLoginClick: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSignupClick(firstName, email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Log in")
        }
    }
}