package com.example.myadventure.model

data class Record(
    val recordId: String = "",
    val title: String = "",
    val description: String = "",
    val image: String = "", // 단일 이미지 필드
    val authorUid: String = "",
    val partnerUid: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
