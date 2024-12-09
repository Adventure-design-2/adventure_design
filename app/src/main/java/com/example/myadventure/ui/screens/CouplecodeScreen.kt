package com.example.myadventure.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.myadventure.viewmodel.AuthViewModel
import com.example.myadventure.viewmodel.InviteViewModel
import kotlinx.coroutines.launch

@Composable
fun CouplecodeScreen(
    navController: NavController,
    viewModel: InviteViewModel,
    authViewModel: AuthViewModel,
    userUid: String
) {
    var inputCode by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var inviteCode by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    // Firestore에서 초대 코드 로드 및 생성
    LaunchedEffect(userUid) {
        coroutineScope.launch {
            authViewModel.loadUserProfile { profile ->
                inviteCode = profile?.inviteCode.orEmpty()
                if (inviteCode.isEmpty()) {
                    coroutineScope.launch {
                        val generatedCode = viewModel.generateInviteCode()
                        authViewModel.updateInviteCode(userUid, generatedCode) { success ->
                            if (success) {
                                inviteCode = generatedCode
                                viewModel.updateInviteState(userUid)
                            } else {
                                statusMessage = "초대 코드 생성 실패."
                            }
                        }
                    }
                } else {
                    viewModel.updateInviteState(userUid)
                }
                loading = false
            }
        }
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
                    text = inviteCode,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.weight(2f)
                )
                IconButton(
                    onClick = {
                        val clipboardManager = context.getSystemService(ClipboardManager::class.java)
                        val clipData = ClipData.newPlainText("MyCode", inviteCode)
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
                coroutineScope.launch {
                    isLoading = true
                    val isConnected = authViewModel.connectPartner(userUid, inputCode)
                    isLoading = false
                    if (isConnected) {
                        statusMessage = "연동 성공! 파트너가 연결되었습니다."
                        navController.navigate("dday_screen") {
                            popUpTo("couplecode_Screen") { inclusive = false }
                        }
                    } else {
                        statusMessage = "연동 실패. 초대 코드를 확인하세요."
                    }
                }
            },
            enabled = inputCode.isNotEmpty() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF776CC))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("확인", color = Color.White, fontSize = 16.sp)
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // 상태 메시지 표시
        if (statusMessage.isNotEmpty()) {
            Text(text = statusMessage, color = if (statusMessage.contains("성공")) Color.Green else Color.Red)
        }
    }
}