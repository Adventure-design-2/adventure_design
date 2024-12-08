package com.example.myadventure.data

import com.example.myadventure.model.ChatRoom
import com.example.myadventure.model.Mission
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
                        mission = mission
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
     * @param userId 사용자 ID
     * @param onResult 결과 콜백
     */
    fun getChatRooms(userId: String, onResult: (List<ChatRoom>) -> Unit) {
        database.child("chatRooms")
            .get()
            .addOnSuccessListener { snapshot ->
                val chatRooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                    .filter { it.user1 == userId || it.user2 == userId }
                onResult(chatRooms)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
