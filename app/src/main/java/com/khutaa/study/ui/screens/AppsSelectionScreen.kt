package com.khutaa.study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khutaa.study.data.InstalledApp
import com.khutaa.study.data.InstalledAppsHelper

@Composable
fun AppsSelectionScreen(
    initiallySelected: Set<String>,
    onFinish: (selectedPackages: Set<String>) -> Unit
) {
    val context = LocalContext.current
    val allApps = remember { InstalledAppsHelper.getLaunchableApps(context) }
    var selected by remember { mutableStateOf(initiallySelected) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("اختر التطبيقات التي تشتت انتباهك", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "لن تتمكن من فتح هذه التطبيقات إلى أن تنتهي جلسة المذاكرة.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "${selected.size} تطبيقات مختارة",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allApps, key = { it.packageName }) { app: InstalledApp ->
                    val isChecked = app.packageName in selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(app.label, modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selected = if (checked) selected + app.packageName
                                else selected - app.packageName
                            }
                        )
                    }
                    Divider()
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "بياناتك آمنة ولا يتم الوصول لأي محتوى داخل هذه التطبيقات.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onFinish(selected) },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("تفعيل وضع التركيز") }
        }
    }
}
