package com.khutaa.study.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class HomeUiState(
    val name: String,
    val dailyGoalMinutes: Int,
    val blockedAppsCount: Int,
    val sessionsToday: Int,
    val minutesToday: Int
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    onStartSession: (durationMinutes: Int, subject: String) -> Unit,
    onEditApps: () -> Unit
) {
    val context = LocalContext.current
    var subject by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(25) }
    val durationOptions = listOf(15, 25, 45, 60)

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("أهلًا، ${state.name}", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "هدفك اليومي: ${state.dailyGoalMinutes} دقيقة",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("${state.sessionsToday}", "جلسات اليوم", Modifier.weight(1f))
                StatCard("${state.minutesToday}", "دقائق اليوم", Modifier.weight(1f))
                StatCard("${state.blockedAppsCount}", "تطبيقات مقيّدة", Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            Text("ابدأ جلسة تركيز", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("المادة (اختياري)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                durationOptions.forEach { minutes ->
                    FilterChip(
                        selected = duration == minutes,
                        onClick = { duration = minutes },
                        label = { Text("$minutes د") }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onStartSession(duration, subject.ifBlank { "جلسة تركيز" }) },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("ابدأ الآن") }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onEditApps,
                modifier = Modifier.fillMaxWidth()
            ) { Text("تعديل قائمة الالتزام") }

            Spacer(Modifier.height(10.dp))
            TextButton(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            ) { Text("تفعيل صلاحية القفل من إعدادات الجهاز") }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
