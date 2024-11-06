package com.example.myadventure.ui.compose

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.saveable.*

@Composable
fun DDayCounter() {
    val context = LocalContext.current
    var dDay by rememberSaveable { mutableStateOf("D-DAY") }
    var remainingDays by rememberSaveable { mutableStateOf("") }
    val calendar = Calendar.getInstance()

    Button(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    dDay = "${year}.${month + 1}.${dayOfMonth}"
                    val today = Calendar.getInstance()
                    val diffInMillis = calendar.timeInMillis - today.timeInMillis
                    val diff = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                    remainingDays = if (diff >= 0) "D-$diff" else "D+${-diff}"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = dDay, style = MaterialTheme.typography.bodyLarge)
            if (remainingDays.isNotEmpty()) {
                Text(text = remainingDays, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
