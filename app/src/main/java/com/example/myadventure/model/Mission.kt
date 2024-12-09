package com.example.myadventure.model

data class Mission(
    val title: String = "",
    val environment: Int = 0,
    val locationTag: List<String> = emptyList(),
    val detail: String = "",
    val completedCount: Int = 0,
    val action: String = ""
)
