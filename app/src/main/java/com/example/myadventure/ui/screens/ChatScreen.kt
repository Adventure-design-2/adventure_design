package com.example.myadventure.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.model.ChatMessage
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(chatRoomId: String, currentUserId: String) {
    val database = FirebaseDatabase.getInstance().reference
    val storage = FirebaseStorage.getInstance().reference
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 메시지 로드
    LaunchedEffect(chatRoomId) {
        val chatRef = database.child("chatRooms").child(chatRoomId).child("messages")
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                if (message != null) {
                    messages.add(message)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages) { message ->
                Column(modifier = Modifier.padding(4.dp)) {
                    Text(text = "${message.senderId}: ${message.message}")
                    message.imageUrl?.let { imageUrl ->
                        // 이미지 로드
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Image",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
        }

        var inputMessage by remember { mutableStateOf("") }

        // 이미지 선택 런처
        val imagePickerLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val imageRef = storage.child("chatRooms/$chatRoomId/${System.currentTimeMillis()}.jpg")
                val uploadTask = imageRef.putFile(uri)

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val newMessage = ChatMessage(
                            id = database.push().key ?: "",
                            senderId = currentUserId,
                            imageUrl = downloadUri.toString()
                        )

                        database.child("chatRooms")
                            .child(chatRoomId)
                            .child("messages")
                            .child(newMessage.id)
                            .setValue(newMessage)
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(8.dp)) {
            BasicTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            Button(onClick = {
                val newMessage = ChatMessage(
                    id = database.push().key ?: "",
                    senderId = currentUserId,
                    message = inputMessage
                )

                database.child("chatRooms")
                    .child(chatRoomId)
                    .child("messages")
                    .child(newMessage.id)
                    .setValue(newMessage)

                inputMessage = ""
            }) {
                Text("Send")
            }

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Image")
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(chatRoomId = "testRoom", currentUserId = "user123")
}
