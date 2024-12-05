package com.example.myadventure.data

import com.example.myadventure.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // 현재 로그인한 사용자 UID 가져오기
    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    // 유저 프로필 저장
    suspend fun saveUserProfile(userProfile: UserProfile) {
        val uid = userProfile.uid
        if (uid.isNotEmpty()) {
            firestore.collection("users").document(uid).set(userProfile).await()
        }
    }

    // 유저 프로필 로드
    suspend fun loadUserProfile(uid: String): UserProfile? {
        val document = firestore.collection("users").document(uid).get().await()
        return document.toObject(UserProfile::class.java)
    }
}
