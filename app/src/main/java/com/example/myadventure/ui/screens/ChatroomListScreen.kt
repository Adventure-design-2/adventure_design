package com.example.myadventure.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.model.ChatRoom
import com.example.myadventure.viewmodel.AuthViewModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ChatRoomListScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    onRoomSelected: (String) -> Unit
) {
    val chatRooms = remember { mutableStateListOf<ChatRoom>() }
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // 현재 사용자 로드 및 Firebase에서 채팅방 가져오기
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile { profile ->
            currentUser = profile?.uid ?: ""
            if (currentUser.isNotBlank()) {
                val database = FirebaseDatabase.getInstance().reference
                database.child("chatRooms")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val rooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                            .filter { it.user1 == currentUser || it.user2 == currentUser }
                        chatRooms.clear()
                        chatRooms.addAll(rooms)
                        isLoading = false
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "추억 불러오기 실패: ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        isLoading = false
                    }
            } else {
                Toast.makeText(context, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFFFF4F7),
        bottomBar = {
            BottomNavigationBar(navController = navController) // 바텀 네비게이션 바 추가
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF4F7))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // 화면 제목
                Text(
                    text = "추억저장소",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    Text(
                        text = "추억 가져오는 중...",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (chatRooms.isEmpty()) {
                    // 데이터가 없을 경우 메시지 표시
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "아직 추억이 없습니다!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(chatRooms) { chatRoom ->
                            ChatRoomPolaroidCard(chatRoom = chatRoom) {
                                onRoomSelected(chatRoom.id)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ChatRoomPolaroidCard(
    chatRoom: ChatRoom,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.White)
                .border(2.dp, Color.LightGray) // 경계선 추가
        ) {
            // 랜덤 이미지 선택
            val randomImageUrl = chatRoom.imageUrl.randomOrNull() // 랜덤 URL 가져오기
            val context = LocalContext.current
            if (randomImageUrl != null) {
                val painter = rememberAsyncImagePainter(
                    model = randomImageUrl,
                    onError = { // 에러 처리: 기본 이미지 표시
                        Toast.makeText(context, "이미지 로드 실패", Toast.LENGTH_SHORT).show()
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = "Chat Room Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            } else {
                // 기본 이미지 표시
                Image(
                    painter = rememberAsyncImagePainter(model = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Default Chat Room Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
        }

        // 채팅방의 미션 제목 표시
        Text(
            text = chatRoom.mission?.title ?: "추억",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            maxLines = 1
        )
    }
}
