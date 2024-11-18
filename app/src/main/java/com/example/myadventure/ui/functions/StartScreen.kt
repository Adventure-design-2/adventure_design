package com.example.myadventure.ui.functions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun StartScreen(navController: NavController) {
    // 다음 화면으로 이동하는 조건을 위한 상태 변수
    var navigateToNextScreen by remember { mutableStateOf(false) }

    // 일정 시간 후 다음 화면으로 자동 이동
    LaunchedEffect(Unit) {

        delay(2000) // 2초 대기 후
        navigateToNextScreen = true
    }

    // navigateToNextScreen이 true일 때 signup_screen으로 이동
    LaunchedEffect(navigateToNextScreen) {
        if (navigateToNextScreen) {
            navController.navigate("signup_screen") {
                popUpTo("start_screen") { inclusive = true } // 현재 화면을 스택에서 제거
            }

        }
    }

    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "!!!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}
