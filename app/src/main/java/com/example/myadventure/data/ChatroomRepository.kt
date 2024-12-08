package com.example.myadventure.data

import com.example.myadventure.model.ChatRoom
import com.google.firebase.database.FirebaseDatabase

class ChatRoomRepository {
    private val database = FirebaseDatabase.getInstance().reference

    fun createChatRoom(user1: String, user2: String, onResult: (Boolean) -> Unit) {
        val roomId = database.child("chatRooms").push().key ?: return
        val chatRoom = ChatRoom(
            id = roomId,
            name = "$user1 & $user2",
            user1 = user1,
            user2 = user2
        )
        database.child("chatRooms").child(roomId).setValue(chatRoom)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
