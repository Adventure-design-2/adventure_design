package com.example.myadventure.ui.screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myadventure.R
import com.example.myadventure.viewmodel.InviteViewModel


@Composable
fun CouplecodeScreen(navController: NavController, viewModel: InviteViewModel) {
    var inputCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    // 초대 코드 상태 관리
    var myCode by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    // 초대 코드 생성
    LaunchedEffect(Unit) {
        viewModel.generateInviteCode("currentUserUid") // 여기에 현재 사용자의 UID를 전달
    }

    // ViewModel에서 초대 코드 상태를 수신
    val generatedCode by viewModel.inviteState.collectAsState()

    if (generatedCode != null && myCode != generatedCode) {
        myCode = generatedCode ?: ""
        loading = false
    }

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

        if (loading) {
            CircularProgressIndicator()
        } else {
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
                        val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                        val clipData = ClipData.newPlainText("MyCode", myCode)
                        clipboardManager?.setPrimaryClip(clipData)

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
        }

        Spacer(modifier = Modifier.height(80.dp)) // 위치를 더 밑으로 내리기 위해 추가 여백

        // 상대방 코드 입력
        Text(
            text = "상대방의 커플 코드를 입력해주세요.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 나머지 코드는 그대로 유지
        BasicTextField(
            value = inputCode,
            onValueChange = { inputCode = it },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .border(1.dp, Color(0xFFD1D1D1), shape = RectangleShape)
                .background(Color.White),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.validateInviteCode("currentUserUid", inputCode,
                    onSuccess = { partnerUid ->
                        Toast.makeText(context, "연동 성공! 파트너: $partnerUid", Toast.LENGTH_SHORT).show()
                        navController.navigate("DDayScreen") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        }
                    },
                    onError = { error ->
                        Toast.makeText(context, "연동 실패: $error", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF776CC))
        ) {
            Text(
                text = "확인",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}



