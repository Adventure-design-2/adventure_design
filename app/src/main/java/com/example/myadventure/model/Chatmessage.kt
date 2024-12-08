// 위치: com.example.myadventure.model
package com.example.myadventure.model


data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val imageUrl: String? = null, // 이미지 URL 필드 추가
    val timestamp: Long = System.currentTimeMillis()
)

