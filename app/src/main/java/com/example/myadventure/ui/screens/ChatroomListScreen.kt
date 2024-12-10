package com.example.myadventure.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
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
                    .orderByChild("timestamp") // `timestamp` 필드를 기준으로 정렬
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val rooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                            .filter { it.user1 == currentUser || it.user2 == currentUser }
                            .sortedByDescending { it.timestamp } // 가장 최근 기록이 맨 위로 오도록 정렬
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
        containerColor = Color(0xFFFFF4F7), // 핑크 배경색
        bottomBar = {
            BottomNavigationBar(navController = navController) // 바텀 네비게이션 바 추가
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF4F7)) // 핑크 배경색
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
                            text = "아직 추억이 없습니다!\n새로운 추억을 만들어보아요!!!",
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp) // 바텀 네비게이션 바 공간 확보
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
    val randomImageUrl = chatRoom.imageUrl.randomOrNull()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(Color.White)
                .border(width = 2.dp, color = Color.Gray)
        ) {
            if (!randomImageUrl.isNullOrEmpty()) {
                val painter = rememberAsyncImagePainter(
                    model = randomImageUrl,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = painterResource(id = android.R.drawable.ic_menu_gallery)
                )

                Image(
                    painter = painter,
                    contentDescription = "Chat Room Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                // Coil 에러 확인
                LaunchedEffect(randomImageUrl) {
                    painter.state.let { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            Toast.makeText(context, "이미지 로드 실패: $randomImageUrl", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Default Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        }

        // 채팅방 제목 표시
        Text(
            text = chatRoom.mission?.title ?: "추억",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}





