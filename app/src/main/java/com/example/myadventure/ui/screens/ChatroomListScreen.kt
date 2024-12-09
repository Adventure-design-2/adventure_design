package com.example.myadventure.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.data.ChatRoomRepository
import com.example.myadventure.model.ChatRoom
import com.example.myadventure.model.Mission
import com.example.myadventure.viewmodel.AuthViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun ChatRoomListScreen(
    viewModel: AuthViewModel,
    onRoomSelected: (String) -> Unit
) {
    val chatRooms = remember { mutableStateListOf<ChatRoom>() }
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) } // 로딩 상태 추가

    // 현재 사용자 로드 및 Firebase 쿼리
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile { profile ->
            currentUser = profile?.uid ?: ""
            if (currentUser.isNotBlank()) {
                // Firebase에서 관련 채팅방 가져오기
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
                            "채팅방 로드 실패: ${it.message}",
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

    // UI 구성
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 화면 제목
        Text(text = "Chat Rooms", modifier = Modifier.padding(bottom = 8.dp))

        if (isLoading) {
            // 로딩 중 표시
            Text(text = "Loading chat rooms...", color = Color.Gray)
        } else if (chatRooms.isEmpty()) {
            // 데이터가 없는 경우 메시지 표시
            Text(text = "No chat rooms found.", color = Color.Gray)
        } else {
            // 채팅방 리스트
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatRooms) { chatRoom ->
                    ChatRoomCard(chatRoom = chatRoom) {
                        onRoomSelected(chatRoom.id)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRoomCard(
    chatRoom: ChatRoom,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        // 채팅방 대표 이미지
        if (!chatRoom.imageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(chatRoom.imageUrl),
                contentDescription = "Chat Room Image",
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 8.dp)
            )
        }

        // 채팅방 정보
        Column {
            Text(text = chatRoom.name, modifier = Modifier.padding(bottom = 4.dp))
            chatRoom.mission?.let {
                Text(text = "Mission: ${it.title}", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = "Participants: ${chatRoom.user1}, ${chatRoom.user2}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}


