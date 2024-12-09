package com.example.myadventure.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DDayRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // D-Day 저장
    suspend fun saveDDay(userUid: String, dDay: String): Boolean {
        return try {
            firestore.collection("users")
                .document(userUid)
                .update("dday", dDay)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // D-Day 가져오기
    suspend fun getDDay(userUid: String): String? {
        return try {
            val snapshot = firestore.collection("users")
                .document(userUid)
                .get()
                .await()
            snapshot.getString("dday")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
