package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myadventure.ui.chat.ChatScreen
import com.example.myadventure.ui.screens.ChatRoomListScreen
import com.example.myadventure.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = AuthViewModel()
            var selectedChatRoomId by remember { mutableStateOf<String?>(null) }

            if (selectedChatRoomId == null) {
                ChatRoomListScreen(viewModel) { roomId ->
                    selectedChatRoomId = roomId
                }
            } else {
                ChatScreen(chatRoomId = selectedChatRoomId!!, currentUserId = viewModel.getCurrentUserId())
            }
        }
    }
}
