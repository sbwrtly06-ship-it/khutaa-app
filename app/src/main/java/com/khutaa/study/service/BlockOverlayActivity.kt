package com.khutaa.study.service

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khutaa.study.MainActivity
import com.khutaa.study.data.SessionManager
import com.khutaa.study.ui.theme.KhutaaTheme
import kotlinx.coroutines.delay

/**
 * Full-screen reminder shown instead of a blocked app while a focus
 * session is active. This is the in-app "lock" experience -- it cannot
 * technically prevent someone from pressing the recents button and
 * reopening the app; committing is on the person, same as any
 * accessibility-based blocker.
 */
class BlockOverlayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_BLOCKED_APP = "blocked_app_package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhutaaTheme {
                BlockScreen(
                    subject = SessionManager.subject(this),
                    onOpenApp = {
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finish()
                    },
                    onSessionEnded = { finish() }
                )
            }
        }
    }

    override fun onBackPressed() {
        // Swallow back presses so the blocked app underneath isn't revealed;
        // send the user to the home screen instead.
        moveTaskToBack(true)
    }
}

@Composable
private fun BlockScreen(
    subject: String,
    onOpenApp: () -> Unit,
    onSessionEnded: () -> Unit
) {
    val context = LocalContext.current
    var remaining by remember { mutableStateOf(SessionManager.remainingMillis(context)) }

    LaunchedEffect(Unit) {
        while (remaining > 0) {
            delay(1000)
            remaining = SessionManager.remainingMillis(context)
        }
        onSessionEnded()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("وضع التركيز مفعّل", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            if (subject.isNotBlank()) {
                Text(subject, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))
            }
            val totalSeconds = remaining / 1000
            val mm = (totalSeconds / 60).toString().padStart(2, '0')
            val ss = (totalSeconds % 60).toString().padStart(2, '0')
            Text(
                "$mm:$ss",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "هذا التطبيق ضمن قائمة الالتزام اللي حددتها. ارجع لهذا التطبيق بعد ما تخلص جلستك.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = onOpenApp) {
                Text("الرجوع لتطبيق خُطى")
            }
        }
    }
}
