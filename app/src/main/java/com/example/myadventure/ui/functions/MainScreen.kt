package com.example.myadventure.ui.functions

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myadventure.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current

    // 전체 화면 배경 색
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F7)) // 연한 분홍색 배경
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp)) // 상단 여백

            // 상단 텍스트
            Text(
                text = "똑딱!\n오늘의 데이트 요정이 등장했어요 ✨\n두 분께 딱 맞는 미션 추천, 받아보실래요?",
                fontSize = 18.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontWeight = FontWeight.Bold
            )

            // 요정 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_character_ex), // 리소스 이미지 이름
                contentDescription = "character",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )

            // 버튼들
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "기록지 보기 클릭됨", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)) // 분홍색 버튼
                ) {
                    Text(text = "기록지 보러가기", fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        navController.navigate("MissionScreen") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)) // 분홍색 버튼
                ) {
                    Text(text = "미션 하러가기", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun MissionScreen(navController: NavController) {
    // MissionScreen UI 정의
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF)), // 연한 하늘색 배경
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "미션 화면입니다!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun AppNavigation() {
    val navController = androidx.navigation.compose.rememberNavController()

    NavHost(navController = navController, startDestination = "MainScreen") {
        composable("MainScreen") { MainScreen(navController) }
        composable("MissionScreen") { MissionScreen(navController) }
    }
}
