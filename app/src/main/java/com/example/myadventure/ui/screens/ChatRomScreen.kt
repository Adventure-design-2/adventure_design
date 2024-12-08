package com.example.myadventure.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.model.ChatMessage
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

@Composable
fun ChatRoomScreen(
    navController: NavController,
    roomId: String
) {
    val database = FirebaseDatabase.getInstance().reference
    val storage = FirebaseStorage.getInstance().reference
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputMessage by remember { mutableStateOf("") }
    var missionTitle by remember { mutableStateOf("Loading...") } // 미션 제목 초기 상태

    // 미션 제목 로드
    LaunchedEffect(roomId) {
        val roomRef = database.child("chatRooms").child(roomId)
        roomRef.child("mission").get().addOnSuccessListener { snapshot ->
            val mission = snapshot.child("title").getValue(String::class.java)
            missionTitle = mission ?: "미션 제목 없음"
        }.addOnFailureListener {
            missionTitle = "미션 제목 로드 실패"
        }
    }

    // 메시지 로드
    LaunchedEffect(roomId) {
        val chatRef = database.child("chatRooms").child(roomId).child("messages")
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

    // 이미지 선택 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val imageRef = storage.child("chatRooms/$roomId/${System.currentTimeMillis()}.jpg")
                val uploadTask = imageRef.putFile(uri)

                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val newMessage = ChatMessage(
                            id = database.push().key ?: "",
                            senderId = "currentUser",
                            imageUrl = downloadUri.toString()
                        )

                        database.child("chatRooms")
                            .child(roomId)
                            .child("messages")
                            .child(newMessage.id)
                            .setValue(newMessage)
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단 바
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // 미션 제목 가운데 정렬
            Text(
                text = missionTitle,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // 미션 완료 버튼
            Button(
                onClick = {
                    navController.navigate("chat_room_list_screen") {
                        popUpTo("chat_room_screen/$roomId") { inclusive = true }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "미션 완료")
            }
        }

        // 메시지 리스트
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages) { message ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (message.senderId == "currentUser") Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = if (message.senderId == "currentUser") Color(0xFFDFFFD6) else Color(0xFFF0F0F0),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        message.message?.let {
                            Text(text = it)
                        }
                        message.imageUrl?.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Image",
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 메시지 입력 및 전송
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 입력창
            BasicTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(48.dp), // 입력창 높이 조정
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF6F6F6), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (inputMessage.isEmpty()) {
                            Text(text = "메시지를 입력하세요...", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )

            // 전송 및 이미지 버튼
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        if (inputMessage.isNotBlank()) {
                            val newMessage = ChatMessage(
                                id = database.push().key ?: "",
                                senderId = "currentUser",
                                message = inputMessage
                            )

                            // Firebase에 메시지 추가
                            database.child("chatRooms")
                                .child(roomId)
                                .child("messages")
                                .child(newMessage.id)
                                .setValue(newMessage)

                            inputMessage = "" // 입력 필드 초기화
                        }
                    },
                    modifier = Modifier.size(64.dp, 32.dp) // 버튼 크기 조정
                ) {
                    Text("전송", fontSize = 12.sp)
                }

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.size(64.dp, 32.dp) // 버튼 크기 조정
                ) {
                    Text("이미지", fontSize = 12.sp)
                }
            }
        }
    }
}



