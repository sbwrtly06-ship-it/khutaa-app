package com.khutaa.study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val presets = listOf(30, 60, 90, 120, 180, 240) // minutes

@Composable
fun DurationScreen(
    initialMinutes: Int,
    onNext: (minutes: Int) -> Unit
) {
    var selected by remember { mutableStateOf(initialMinutes) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("ما مدة المذاكرة اليومية التي تناسبك؟", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "اختر المدة التي تريد أن تذاكرها كل يوم لتحقيق هدفك.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(presets) { minutes ->
                    val isSelected = minutes == selected
                    val label = if (minutes % 60 == 0) "${minutes / 60} ساعة" else "$minutes د"
                    OutlinedCard(
                        onClick = { selected = minutes },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Text(label, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }

            Button(
                onClick = { onNext(selected) },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("التالي") }
        }
    }
}
