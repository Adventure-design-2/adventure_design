package com.example.myadventure.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val bio: String = "",
    val imageUrl: String = "",
    val partnerUid: String = "",
    val inviteCode: String = ""
)
