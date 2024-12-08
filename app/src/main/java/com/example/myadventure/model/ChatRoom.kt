package com.example.myadventure.model

data class ChatRoom(
    val id: String = "",
    val name: String = "",
    val user1: String = "",
    val user2: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val mission: Mission? =null,
    val imageUrl: String? = null // 채팅방 대표 이미지 URL
)

