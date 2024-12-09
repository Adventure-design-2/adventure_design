package com.example.myadventure.model

data class Record(
    var recordId: String = "",
    val title: String = "",
    val description: String = "",
    val image: String = "", // 단일 이미지
    val authorUid: String = "",
    val partnerUid: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
