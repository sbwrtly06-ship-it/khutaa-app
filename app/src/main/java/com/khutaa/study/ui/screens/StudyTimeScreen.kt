package com.khutaa.study.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudyTimeScreen(
    initialHour: Int,
    initialMinute: Int,
    onNext: (hour: Int, minute: Int) -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("متى تريد أن تبدأ المذاكرة يوميًا؟", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                "سيتم تفعيل وضع التركيز في هذا الوقت تلقائيًا كل يوم.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberStepper(value = hour, range = 0..23, onChange = { hour = it }, label = "ساعة")
                Spacer(Modifier.width(16.dp))
                Text(":", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.width(16.dp))
                NumberStepper(value = minute, range = 0..59, step = 5, onChange = { minute = it }, label = "دقيقة")
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onNext(hour, minute) },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("التالي") }
        }
    }
}

@Composable
private fun NumberStepper(
    value: Int,
    range: IntRange,
    step: Int = 1,
    onChange: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            val next = value + step
            onChange(if (next > range.last) range.first else next)
        }) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "زيادة") }

        Text(
            value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineMedium
        )

        IconButton(onClick = {
            val prev = value - step
            onChange(if (prev < range.first) range.last else prev)
        }) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "نقصان") }

        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
