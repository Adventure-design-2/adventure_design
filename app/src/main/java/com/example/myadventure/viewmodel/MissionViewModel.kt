package com.example.myadventure.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myadventure.data.UserRepository
import com.example.myadventure.model.RecordRoom
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class MissionViewModel(
    private val firebaseService: FirebaseService,
    private val userRepository: UserRepository
) : ViewModel() {

    // 새 채팅방 생성 함수
    fun createChatRoomForMission(missionTitle: String, partnerUid: String, onSuccess: (String) -> Unit) {
        val currentUser = userRepository.getCurrentUser()
        val roomId = firebaseService.generateRoomId()

        // RecordRoom 데이터
        val roomData = mapOf(
            "title" to missionTitle,
            "participants" to listOf(currentUser.uid, partnerUid),
            "timestamp" to System.currentTimeMillis()
        )

        firebaseService.createRoom(roomId, roomData) { success ->
            if (success) {
                // 현재 사용자의 activeRooms 업데이트
                userRepository.updateActiveRooms(currentUser.uid, roomId)
                userRepository.updateActiveRooms(partnerUid, roomId)
                onSuccess(roomId)
            }
        }
    }

    fun createChatRoom(missionTitle: String, onRoomCreated: (String?) -> Unit) {
        // 현재 사용자 정보 가져오기
        val currentUser = userRepository.getCurrentUser()
        val partnerUid = currentUser.partnerUid

        if (partnerUid.isNullOrEmpty()) {
            onRoomCreated(null)
            return
        }

        val roomId = firebaseService.generateRoomId()

        // RecordRoom 데이터
        val roomData = mapOf(
            "title" to missionTitle,
            "participants" to listOf(currentUser.uid, partnerUid),
            "timestamp" to System.currentTimeMillis()
        )

        firebaseService.createRoom(roomId, roomData) { success ->
            if (success) {
                // Room ID 반환
                onRoomCreated(roomId)
            } else {
                onRoomCreated(null)
            }
        }
    }


}

class FirebaseService {
    fun generateRoomId(): String = FirebaseDatabase.getInstance().reference.push().key ?: UUID.randomUUID().toString()

    fun createRoom(roomId: String, roomData: Map<String, Any>, onComplete: (Boolean) -> Unit) {
        FirebaseDatabase.getInstance()
            .getReference("rooms")
            .child(roomId)
            .setValue(roomData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

}
