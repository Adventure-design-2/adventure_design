package com.example.myadventure.ui.functions

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.R

@Composable
fun CouplecodeScreen(navController: NavController) {
    // 사용자 입력값 상태 저장
    var inputCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    // "내 코드"를 난수로 생성
    val myCode by remember { mutableStateOf(generateRandomCode()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF5F8)) // 연한 핑크색 배경
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp)) // 위치를 더 밑으로 내리기 위해 여백 추가

        // 내 코드
        Text(
            text = "상대방에게 공유하기",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 24.sp, // 글자 크기 2배로 키움
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFFF8F8F8), shape = RectangleShape)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "내 코드",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = myCode,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(2f)
            )
            IconButton(
                onClick = {
                    // 클립보드 매니저를 사용하여 내 코드 복사
                    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                    val clipData = ClipData.newPlainText("MyCode", myCode)
                    clipboardManager?.setPrimaryClip(clipData)

                    // 복사 완료 알림 표시
                    Toast.makeText(context, "코드가 클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_copy), // 복사 아이콘 리소스 사용
                    contentDescription = "복사",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // 위치를 더 밑으로 내리기 위해 추가 여백

        // 상대방 코드 입력
        Text(
            text = "상대방의 커플 코드를 입력해주세요.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 24.sp, // 글자 크기 2배로 키움
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = inputCode, // 입력값 상태 사용
            onValueChange = { inputCode = it }, // 입력값 변경 처리
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text // 텍스트 입력 가능
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // 입력 칸의 고정 높이
                .padding(horizontal = 16.dp)
                .border(1.dp, Color(0xFFD1D1D1), shape = RectangleShape)
                .background(Color.White),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp, // 글자 크기
                lineHeight = 24.sp, // 글자 높이 조정
                color = Color.Black, // 글자 색상
                textAlign = TextAlign.Center // 텍스트 가운데 정렬
            ),
            singleLine = true, // 여러 줄 입력 방지
            cursorBrush = SolidColor(Color.Black),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center, // 박스의 내용 중앙 정렬
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp) // 내부 텍스트 패딩
                ) {
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp)) // 버튼과 입력 칸 간격 추가

        // 확인 버튼
        Button(
            onClick = { /* 확인 버튼 클릭 처리 */
                // 다음 화면으로 이동
                navController.navigate("DDayScreen") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
                      },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF776CC)) // 핑크색 버튼
        ) {
            Text(
                text = "확인",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

// 코드생성 - 난수 생성 함수
fun generateRandomCode(): String {
    val letters = ('A'..'Z').toList()
    val numbers = ('0'..'9').toList()
    val randomLetters = List(2) { letters.random() }
    val randomNumbers = List(4) { numbers.random() }
    return (randomLetters + randomNumbers).joinToString("")
}

@Preview
@Composable
fun PreviewCouplecodeScreen() {
    val navController = rememberNavController()
    CouplecodeScreen(navController = navController)
}
