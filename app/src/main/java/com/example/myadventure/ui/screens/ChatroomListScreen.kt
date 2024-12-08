package com.example.myadventure.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.data.ChatRoomRepository
import com.example.myadventure.model.ChatRoom
import com.example.myadventure.model.Mission
import com.example.myadventure.viewmodel.AuthViewModel

@Composable
fun ChatRoomListScreen(
    viewModel: AuthViewModel,
    onRoomSelected: (String) -> Unit
) {
    val repository = ChatRoomRepository()
    var currentUser by remember { mutableStateOf("") }
    val chatRooms = remember { mutableStateListOf<ChatRoom>() }

    // 현재 사용자 로드 및 채팅방 가져오기
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile { profile ->
            currentUser = profile?.uid ?: ""
            if (currentUser.isNotBlank()) {
                repository.getChatRooms(currentUser) { rooms ->
                    chatRooms.clear()
                    chatRooms.addAll(rooms)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 화면 제목
        Text(text = "Chat Rooms", modifier = Modifier.padding(bottom = 8.dp))

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




@Composable
fun ChatRoomItem(
    chatRoom: ChatRoom,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = chatRoom.name)
    }
}

@Composable
fun CreateChatRoomSection(
    currentUser: String,
    onRoomCreated: (ChatRoom) -> Unit,
    repository: ChatRoomRepository
) {
    var partnerId by remember { mutableStateOf("") }
    var missionTitle by remember { mutableStateOf("Default Mission") }
    val mission = Mission(
        title = missionTitle,
        environment = 1,
        locationTag = listOf("Example", "Tag"),
        detail = "Example mission detail",
        action = "Perform"
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextField(
            value = partnerId,
            onValueChange = { partnerId = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Enter partner UID") }
        )
        TextField(
            value = missionTitle,
            onValueChange = { missionTitle = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Mission title") }
        )
        Button(onClick = {
            if (currentUser.isNotBlank() && partnerId.isNotBlank()) {
                repository.createChatRoom(
                    user1 = currentUser,
                    user2 = partnerId,
                    mission = mission
                ) { roomId ->
                    if (roomId != null) {
                        val newRoom = ChatRoom(
                            id = roomId,
                            name = "Mission: ${mission.title}",
                            user1 = currentUser,
                            user2 = partnerId,
                            mission = mission
                        )
                        onRoomCreated(newRoom)
                    } else {
                        println("Failed to create chat room.")
                    }
                }
            }
        }) {
            Text("Create")
        }
    }
}
