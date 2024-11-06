package com.example.myadventure.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun PointCounter(userPreferences: UserPreferences = UserPreferences.getInstance(LocalContext.current)) {
    val scope = rememberCoroutineScope()

    // 포인트 Flow 구독 및 초기 값 설정
    val points by userPreferences.pointsFlow.collectAsState(initial = userPreferences.getPoints())

    // 사용자 입력 값
    var inputAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "현재 포인트: $points", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // 포인트 입력 필드
        OutlinedTextField(
            value = inputAmount,
            onValueChange = { inputAmount = it.filter { char -> char.isDigit() } },
            label = { Text("포인트 변경 값") },
            placeholder = { Text("숫자를 입력하세요") },
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val amount = inputAmount.toIntOrNull() ?: 0
                    if (amount > 0) {
                        scope.launch {
                            userPreferences.savePoints(points + amount) // 포인트 증가 후 저장
                        }
                    }
                }
            ) {
                Text(text = "포인트 증가")
            }
            Button(
                onClick = {
                    val amount = inputAmount.toIntOrNull() ?: 0
                    if (    amount in 1..points) {
                        scope.launch {
                            userPreferences.savePoints(points - amount) // 포인트 감소 후 저장
                        }
                    }
                },
                enabled = inputAmount.toIntOrNull()?.let { it in 1..points } == true
            ) {
                Text(text = "포인트 감소")
            }
        }
    }
}
