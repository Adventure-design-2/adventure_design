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
    // 화면이 표시될 때 2초 후에 자동으로 이동하는 LaunchedEffect 설정
    LaunchedEffect(Unit) {
        delay(2000) // 2초 딜레이
        navController.navigate("signup_screen") {
            popUpTo("start_screen") { inclusive = true } // 현재 화면을 스택에서 제거
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
                    text = "My Adventure에 오신 것을 환영합니다!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}
