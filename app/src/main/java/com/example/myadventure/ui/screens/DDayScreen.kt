package com.example.myadventure.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DDayScreen(navController: NavController) {
    val context = LocalContext.current
    var dDayInput by remember { mutableStateOf("") } // 숫자 입력 상태 관리
    val formatTemplate = "YYYY-MM-DD" // 기본 템플릿

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
            // 상단 텍스트
            Text(
                text = "두 분의 기념일을 입력하세요",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 날짜 입력 필드
            OutlinedTextField(
                value = formatInput(dDayInput, formatTemplate),
                onValueChange = { newValue ->
                    // 숫자만 허용
                    val filteredValue = newValue.filter { it.isDigit() }

                    // 최대 8자리 숫자만 입력 가능
                    if (filteredValue.length <= 8) {
                        dDayInput = filteredValue
                    }
                },
                placeholder = { Text(text = formatTemplate) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, // 한 줄로 제한
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 확인 버튼
            Button(
                onClick = {
                    val formattedDate = formatInput(dDayInput, formatTemplate)

                    navController.navigate("mission_screen") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)), // 분홍색 버튼
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
        }
    }
}

// 입력된 숫자를 "YYYY-MM-DD" 형식에 맞게 반영
fun formatInput(input: String, template: String): String {
    val result = StringBuilder(template)
    var inputIndex = 0

    for (i in template.indices) {
        if (template[i] in listOf('Y', 'M', 'D')) {
            if (inputIndex < input.length) {
                result[i] = input[inputIndex] // 입력값을 템플릿에 덮어쓰기
                inputIndex++
            }
        } else if (template[i] == '-') {
            // '-'는 그대로 유지
            result[i] = '-'
        }
    }

    return enforceTwoDigitFormat(result.toString())
}

// MM-DD 부분을 두 자리 형식으로 강제 변환
fun enforceTwoDigitFormat(input: String): String {
    val parts = input.split("-")

    if (parts.size == 3) {
        val year = parts[0].padEnd(4, 'Y') // 연도는 4자리로 유지
        val month = if (parts[1].length == 1) "0${parts[1]}" else parts[1] // 월을 2자리로 강제
        val day = if (parts[2].length == 1) "0${parts[2]}" else parts[2] // 일을 2자리로 강제
        return "$year-$month-$day"
    }
    return input
}

@Preview
@Composable
fun PreviewDDayScreen() {
    val navController = rememberNavController()
    DDayScreen(navController = navController)
}