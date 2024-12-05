package com.example.myadventure.model

data class Record(
    val recordId: String = "",
    val title: String = "",
    val description: String = "",
    val images: List<String> = listOf(),
    val authorUid: String = "",
    val partnerUid: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
