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

data class RecordRoom(
    var roomId: String = "", // 채팅방 ID
    val title: String = "",
    val participants: List<String> = emptyList(), // 참여자 UID
    val timestamp: Long = System.currentTimeMillis()
)