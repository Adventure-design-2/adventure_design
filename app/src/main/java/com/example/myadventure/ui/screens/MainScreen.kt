package com.example.myadventure.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, dDayResult: String) {
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

//            // D-Day 결과 표시
//            Text(
//                text = "현재 D-Day 상태: $dDayResult",
//                fontSize = 16.sp,
//                color = Color.DarkGray,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )

            // 캐릭터 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_char_main), // 리소스 이미지 이름
                contentDescription = "character",
                modifier = Modifier
                    .size(300.dp)
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
                        navController.navigate("MissionScreen/$dDayResult") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3))
                ) {
                    Text(text = "미션 하러가기", fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    val navController = rememberNavController()
    MainScreen(navController = navController, dDayResult = "D+3") // 미리보기에서 예제 값 전달
}
