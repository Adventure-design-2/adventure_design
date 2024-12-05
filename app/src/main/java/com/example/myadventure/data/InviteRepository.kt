package com.example.myadventure.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class InviteRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // 초대 코드 생성
    suspend fun generateInviteCode(uid: String): String {
        val inviteCode = uid.takeLast(6) // UID의 마지막 6자리로 초대 코드 생성
        firestore.collection("invites").document(inviteCode).set(mapOf("uid" to uid)).await()
        return inviteCode
    }

    // 초대 코드 검증 및 연동
    suspend fun validateAndLinkInviteCode(uid: String, inviteCode: String): String? {
        val snapshot = firestore.collection("invites").document(inviteCode).get().await()
        val partnerUid = snapshot.getString("uid")
        if (partnerUid != null) {
            // 파트너 UID를 업데이트
            firestore.collection("users").document(uid).update("partnerUid", partnerUid).await()
            firestore.collection("users").document(partnerUid).update("partnerUid", uid).await()
            return partnerUid
        }
        return null
    }
}
