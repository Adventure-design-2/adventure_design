package com.example.myadventure.model

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val bio: String = "",
    val imageUrl: String = "",
    val partnerUid: String = "", // 연결된 파트너 ID
    val inviteCode: String = "", // 초대 코드
    val activeRooms: List<String> = emptyList(), // 참여 중인 기록 방 ID
    val lastSeen: Long = System.currentTimeMillis(), // 마지막 활동 시간
    val partnerName: String = ""  // 상대방 이름을 추가
)
