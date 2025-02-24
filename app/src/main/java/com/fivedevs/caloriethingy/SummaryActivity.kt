import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fivedevs.caloriethingy.ApiClient
import com.fivedevs.caloriethingy.LoginActivity
import com.fivedevs.caloriethingy.api.ApiService
import kotlinx.coroutines.launch

class SummaryActivity : ComponentActivity() {
    private val PREFS_NAME = "app_prefs"
    private val TOKEN_KEY = "auth_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val token = prefs.getString(TOKEN_KEY, null)
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            SummaryScreen(token)
        }
    }
}

@Composable
fun SummaryScreen(token: String) {
    var summaryText by remember { mutableStateOf("Loading summary...") }
    val scope = rememberCoroutineScope()
    val apiService = ApiClient.getClient().create(ApiService::class.java)
    val context = LocalContext.current // Get the current context (activity)

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = apiService.getDailySummary("Bearer $token")
                if (response.isSuccessful) {
                    val summary = response.body()
                    summaryText = "Total Calories: ${summary?.total_calories ?: 0}"
                } else {
                    Toast.makeText(context, "Failed to get summary", Toast.LENGTH_SHORT).show() // Use context
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = summaryText)
    }
}