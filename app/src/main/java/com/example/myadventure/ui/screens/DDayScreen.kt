package com.example.myadventure.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDayScreen(navController: NavController) {
    var dDayInput by remember { mutableStateOf("") } // YYYY-MM-DD 형식 입력 상태 관리
    var dDayResult by remember { mutableStateOf("") } // D-Day 결과 상태 관리

    // 오늘 날짜 계산
    val todayDate = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F7)) // 연한 분홍색 배경
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "두 분의 기념일을 입력하세요",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = dDayInput,
                onValueChange = { newValue ->
                    if (newValue.length <= 10 && newValue.matches(Regex("[0-9-]*"))) {
                        dDayInput = newValue
                    }
                },
                placeholder = { Text(text = "YYYY-MM-DD") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    dDayResult = calculateDDay(todayDate, dDayInput)
                    navController.navigate("MainScreen/$dDayResult")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "확인",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (dDayResult.isNotEmpty()) {
                Text(
                    text = dDayResult,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

// D-Day 계산 함수
@SuppressLint("SimpleDateFormat")
fun calculateDDay(today: String, targetDate: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val todayDate = sdf.parse(today)
        val targetDateParsed = sdf.parse(targetDate)

        if (todayDate != null && targetDateParsed != null) {
            val diff = (todayDate.time - targetDateParsed.time) / (1000 * 60 * 60 * 24)
            if (diff < 0) {
                return "날짜를 올바르게 입력해주세요"
            }
            if (diff == 0L) {
                "오늘이 기념일입니다! 🎉"
            } else {
                "D+$diff"
            }
        } else {
            "날짜를 올바르게 입력해주세요."
        }
    } catch (e: Exception) {
        "날짜를 올바르게 입력해주세요."
    }
}



