package com.example.myadventure.data

import com.example.myadventure.model.ChatRoom
import com.example.myadventure.model.Mission
import com.example.myadventure.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRoomRepository {

    private val database = FirebaseDatabase.getInstance().reference

    /**
     * 새로운 채팅방 생성
     *
     * @param user1 첫 번째 사용자 UID
     * @param user2 두 번째 사용자 UID
     * @param mission 선택된 미션 데이터
     * @param onResult 생성 결과 콜백 (성공 시 roomId 반환)
     */
    fun createChatRoom(user1: String, user2: String, mission: Mission, onResult: (String?) -> Unit) {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    // Firebase에 연결된 경우, 채팅방 생성 진행
                    val roomId = database.child("chatRooms").push().key
                    if (roomId == null) {
                        println("Failed to generate roomId")
                        onResult(null)
                        return
                    }

                    val chatRoom = ChatRoom(
                        id = roomId,
                        name = "Mission: ${mission.title}",
                        user1 = user1,
                        user2 = user2,
                        mission = mission,
                        timestamp = System.currentTimeMillis() // 현재 시간 기록
                    )

                    database.child("chatRooms").child(roomId).setValue(chatRoom)
                        .addOnSuccessListener {
                            println("Chat room created successfully: $roomId")
                            onResult(roomId)
                        }
                        .addOnFailureListener { e ->
                            println("Failed to create chat room: ${e.message}")
                            onResult(null)
                        }
                } else {
                    // Firebase에 연결되지 않은 경우
                    println("Firebase is not connected.")
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to check Firebase connection: ${error.message}")
                onResult(null)
            }
        })
    }

    /**
     * 모든 채팅방 로드 (특정 사용자와 관련된 채팅방만 필터링 가능)
     *
     * @param onResult 결과 콜백
     */
    fun getChatRooms(onResult: (List<ChatRoom>) -> Unit) {
        val userProfileRef = database.child("userProfiles")
        val chatRoomRef = database.child("chatRooms")

        // 현재 사용자 프로필에서 userUid 및 partnerUid 가져오기
        userProfileRef.child(FirebaseAuth.getInstance().currentUser?.uid ?: "").get()
            .addOnSuccessListener { userProfileSnapshot ->
                val userProfile = userProfileSnapshot.getValue(UserProfile::class.java)
                val userUid = userProfile?.uid
                val partnerUid = userProfile?.partnerUid

                if (userUid != null) {
                    // 관련된 모든 채팅방 로드
                    chatRoomRef.get()
                        .addOnSuccessListener { chatRoomSnapshot ->
                            val chatRooms = chatRoomSnapshot.children.mapNotNull {
                                it.getValue(ChatRoom::class.java)
                            }.filter {
                                it.user1 == userUid || it.user2 == userUid || it.user1 == partnerUid || it.user2 == partnerUid
                            }.sortedByDescending { it.timestamp } // 최신 채팅방이 맨 위로 오도록 정렬
                            onResult(chatRooms)
                        }.addOnFailureListener {
                            onResult(emptyList())
                        }
                } else {
                    onResult(emptyList())
                }
            }.addOnFailureListener {
                onResult(emptyList())
            }
    }

}
