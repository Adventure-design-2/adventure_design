package com.example.myadventure.data

import com.example.myadventure.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val database = FirebaseDatabase.getInstance()


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


    // 현재 사용자 정보 가져오기
    fun getCurrentUser(): UserProfile {
        val currentUser = auth.currentUser
            ?: throw IllegalStateException("로그인한 사용자가 없습니다.")

        // Firebase에서 사용자 데이터 로드 (비동기 작업을 동기화로 처리)
        val snapshot = database.getReference("users").child(currentUser.uid).get().result
        return snapshot.getValue(UserProfile::class.java)
            ?: throw IllegalStateException("사용자 데이터를 가져올 수 없습니다.")
    }

    // 사용자의 activeRooms 업데이트
    fun updateActiveRooms(userId: String, roomId: String) {
        val userRef = database.getReference("users").child(userId).child("activeRooms")
        userRef.get().addOnSuccessListener { snapshot ->
            val activeRooms = snapshot.getValue<List<String>>().orEmpty().toMutableList()
            if (!activeRooms.contains(roomId)) {
                activeRooms.add(roomId)
                userRef.setValue(activeRooms)
            }
        }
    }
}
