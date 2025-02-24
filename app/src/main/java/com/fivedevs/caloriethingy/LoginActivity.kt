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
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private val PREFS_NAME = "app_prefs"
    private val TOKEN_KEY = "auth_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.contains(TOKEN_KEY)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            LoginScreen(
                onLoginClick = { email, password -> handleLogin(email, password) },
                onSignupClick = { startActivity(Intent(this, SignupActivity::class.java)) }
            )
        }
    }

    private fun handleLogin(email: String, password: String) {
        // Use lifecycleScope instead of coroutineScope
        lifecycleScope.launch {
            val apiService = ApiClient.getClient().create(ApiService::class.java)
            try {
                val response = apiService.login(User(email, password))
                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(TOKEN_KEY, token)
                            .apply()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginClick: (String, String) -> Unit, onSignupClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
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
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onSignupClick) {
            Text("Don't have an account? Sign up")
        }
    }
}