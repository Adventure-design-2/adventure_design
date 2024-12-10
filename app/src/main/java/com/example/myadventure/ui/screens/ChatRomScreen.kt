package com.example.myadventure.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.R
import com.example.myadventure.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
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
    val imageUrls = remember { mutableStateListOf<String>() }
    var inputMessage by remember { mutableStateOf("") }
    var missionTitle by remember { mutableStateOf("Loading...") }
    var currentUser by remember { mutableStateOf("") }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) } // 확대된 이미지 URL

    val context = LocalContext.current

    // 현재 사용자 ID 가져오기
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser?.uid ?: ""
    }

    // 미션 제목 로드 및 이미지 리스트 초기화
    LaunchedEffect(roomId) {
        val roomRef = database.child("chatRooms").child(roomId)
        roomRef.child("mission").get().addOnSuccessListener { snapshot ->
            val mission = snapshot.child("title").getValue(String::class.java)
            missionTitle = mission ?: "미션 제목 없음"
        }

        // 이미지 리스트 로드
        roomRef.child("imageUrl").get().addOnSuccessListener { snapshot ->
            val urls = snapshot.children.mapNotNull { it.getValue(String::class.java) }
            imageUrls.clear()
            imageUrls.addAll(urls)
        }
    }

    // 메시지 및 이미지 갤러리 로드
    LaunchedEffect(roomId) {
        val chatRef = database.child("chatRooms").child(roomId).child("messages")
        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                if (message != null) {
                    messages.add(message)
                    message.imageUrl?.let { imageUrls.add(it) }
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
                            senderId = currentUser,
                            imageUrl = downloadUri.toString()
                        )

                        // Firebase에 메시지 저장
                        database.child("chatRooms")
                            .child(roomId)
                            .child("messages")
                            .child(newMessage.id)
                            .setValue(newMessage)

                        // 이미지 URL을 채팅방 imageUrl 필드에 추가
                        database.child("chatRooms").child(roomId).child("imageUrl")
                            .get().addOnSuccessListener { snapshot ->
                                val currentUrls = snapshot.children.mapNotNull { it.getValue(String::class.java) }.toMutableList()
                                currentUrls.add(downloadUri.toString())

                                // 업데이트된 이미지 리스트 저장
                                database.child("chatRooms").child(roomId).child("imageUrl")
                                    .setValue(currentUrls)
                                    .addOnSuccessListener {
                                        imageUrls.add(downloadUri.toString()) // 로컬 상태 업데이트
                                        Toast.makeText(context, "이미지 업로드 및 저장 완료", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "이미지 URL 저장 실패", Toast.LENGTH_SHORT).show()
                                    }
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F8)) // 전체 배경색
            .padding(16.dp)
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = missionTitle,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
                onClick = {
                    navController.navigate("diary_screen")
                },
                modifier = Modifier.size(80.dp, 36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3))
            ) {
                Text(text = "저장!", fontSize = 12.sp)
            }
        }

        // 이미지 갤러리
        if (imageUrls.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUrls) { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Chat Image",
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.White)
                            .clickable { selectedImageUrl = imageUrl } // 이미지 클릭 시 확대
                    )
                }
            }
        }

        // 메시지 리스트
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages) { message ->
                // 메시지 텍스트만 표시
                val isCurrentUser = message.senderId == currentUser

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = if (isCurrentUser) Color(0xFFFFC6D3) else Color(0xFFFCE4EC),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        if (message.message.isNotBlank()) { // 공백 메시지 전송 방지
                            Text(text = message.message, color = if (isCurrentUser) Color.White else Color.Black)
                        }
                    }
                }
            }
        }

        // 메시지 입력 및 전송
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color(0xFFFCE4EC), androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                decorationBox = { innerTextField ->
                    if (inputMessage.isEmpty()) {
                        Text(text = "메시지를 입력하세요...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )

            // 전송 버튼
            Button(
                onClick = {
                    if (inputMessage.trim().isNotEmpty()) { // 공백 메시지 제한
                        val newMessage = ChatMessage(
                            id = database.push().key ?: "",
                            senderId = currentUser,
                            message = inputMessage.trim()
                        )
                        database.child("chatRooms").child(roomId).child("messages")
                            .child(newMessage.id).setValue(newMessage)
                        inputMessage = "" // 입력 필드 초기화
                    }
                },
                modifier = Modifier.size(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = R.drawable.baseline_send_24),
                    contentDescription = "Send Icon"
                )
            }

            // 이미지 버튼
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.size(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = R.drawable.baseline_image_24),
                    contentDescription = "Image Icon"
                )
            }
        }
    }

    // 확대된 이미지 보기
    selectedImageUrl?.let { imageUrl ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { selectedImageUrl = null }, // 클릭 시 닫기
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = "Expanded Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}


