package com.example.myadventure.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myadventure.R

@Composable
fun MainScreen(navController: NavController) {

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
                        navController.navigate("diary_screen") {
                            popUpTo("main_screen") { inclusive = true}
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)) // 분홍색 버튼
                ) {
                    Text(text = "추억 보러가기", fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        navController.navigate("mission_screen") // dDayResult 전달
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)) // 분홍색 버튼
                ){
                    Text(text = "미션 하러가기", fontSize = 16.sp)
                }
            }
        }
    }
}




