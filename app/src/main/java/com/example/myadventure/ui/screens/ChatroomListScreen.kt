package com.example.myadventure.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myadventure.data.ChatRoomRepository
import com.example.myadventure.model.ChatRoom
import com.example.myadventure.viewmodel.AuthViewModel

@Composable
fun ChatRoomListScreen(
    viewModel: AuthViewModel,
    onRoomSelected: (String) -> Unit
) {
    val repository = ChatRoomRepository()
    val currentUser = remember { mutableStateOf("") }
    val chatRooms = remember { mutableStateListOf<ChatRoom>() }
    val partnerId = remember { mutableStateOf("") }

    // 현재 사용자 로드
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile { profile ->
            currentUser.value = profile?.uid ?: ""
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Chat Rooms",
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chatRooms) { chatRoom ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRoomSelected(chatRoom.id) }
                        .padding(16.dp)
                ) {
                    Text(text = chatRoom.name)
                }
            }
        }

        // 새로운 채팅방 생성
        Row(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = partnerId.value,
                onValueChange = { partnerId.value = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter partner UID") }
            )
            Button(
                onClick = {
                    val user1 = currentUser.value
                    val user2 = partnerId.value
                    if (user1.isNotBlank() && user2.isNotBlank()) {
                        repository.createChatRoom(user1, user2) { success ->
                            if (success) {
                                println("Chat room created successfully.")
                            } else {
                                println("Failed to create chat room.")
                            }
                        }
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Create")
            }
        }
    }
}

@Preview
@Composable
fun ChatRoomListScreenPreview() {
    ChatRoomListScreen(viewModel = AuthViewModel(), onRoomSelected = {})
}
