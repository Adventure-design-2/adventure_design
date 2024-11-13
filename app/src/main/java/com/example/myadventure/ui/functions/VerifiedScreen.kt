package com.example.myadventure.ui.functions

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myadventure.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(navController: NavController) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var isCommentEnabled by remember { mutableStateOf(false) } // 코멘트 활성화 상태
    var showSuccessDialog by remember { mutableStateOf(false) } // 성공 팝업 표시 여부
    var showCommentRegisteredDialog by remember { mutableStateOf(false) } // 코멘트 등록 팝업
    var isPhotoReplaced by remember { mutableStateOf(false) } // 사진 아이콘 변경 여부
    var user1CommentText by remember { mutableStateOf("먼저 코멘트를 남겨야\n상대방의 코멘트를 볼 수 있어요!\n함께 소중한 순간을 나눠보세요.") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        topBar = {
            TopAppBar(
                title = { Text("인증사진 기록") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF2E4DA))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 인증 사진 박스 또는 아이콘
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPhotoReplaced) {
                        Icon(
                            painter = painterResource(id = R.drawable.couple),
                            contentDescription = "미션 인증 아이콘",
                            modifier = Modifier.size(100.dp)
                        )
                    } else {
                        Text("미션인증사진", color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 카메라와 갤러리 아이콘
                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "카메라",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    Toast.makeText(context, "카메라 인증 시작!", Toast.LENGTH_SHORT).show()
                                    isCommentEnabled = true // 코멘트 활성화
                                    isPhotoReplaced = true // 사진 아이콘 변경
                                }
                        )
                        Text("카메라", fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_diary),
                            contentDescription = "갤러리",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    Toast.makeText(context, "갤러리 인증 시작!", Toast.LENGTH_SHORT).show()
                                    isCommentEnabled = true // 코멘트 활성화
                                }
                        )
                        Text("갤러리", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 사용자 1의 코멘트 박스
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.unlock),
                            contentDescription = "User1",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user1CommentText,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 사용자 2의 코멘트 입력 박스
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("댓글을 입력하세요...") },
                    enabled = isCommentEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 댓글 등록 버튼
                Button(
                    onClick = {
                        showCommentRegisteredDialog = true
                        user1CommentText = "너랑 같이 카페에서 재밌는 활동을 할 수 있어서 좋았다" // 사용자 1의 텍스트 변경
                    },
                    enabled = isCommentEnabled && commentText.text.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("댓글 등록")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 미션 완료 버튼
                Button(
                    onClick = {
                        showSuccessDialog = true
                        coroutineScope.launch {
                            delay(2000)
                            showSuccessDialog = false
                            navController.navigate("garden_screen") // 정원 화면으로 이동
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("미션 완료")
                }
            }

            // 코멘트 등록 팝업
            if (showCommentRegisteredDialog) {
                AlertDialog(
                    onDismissRequest = { showCommentRegisteredDialog = false },
                    title = { Text("코멘트 등록") },
                    text = { Text("코멘트가 등록되었습니다!") },
                    confirmButton = {
                        TextButton(onClick = { showCommentRegisteredDialog = false }) {
                            Text("확인")
                        }
                    }
                )
            }

            // 미션 성공 팝업
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("미션 성공") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 이미지 추가
                        Image(
                            painter = painterResource(id = R.drawable.ic_reward2), // 이미지 리소스 추가
                            contentDescription = "Success Image",
                            modifier = Modifier.size(100.dp) // 이미지 크기 조절
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("하트코인을 얻었어요!!", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFE91E63))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("X 100", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
                    }
                    },
                    confirmButton = {
                        var showSuccessDialog = remember { mutableStateOf(false) } // 성공 팝업 표시 여부

                    }
                )
            }
        }
    )
}
