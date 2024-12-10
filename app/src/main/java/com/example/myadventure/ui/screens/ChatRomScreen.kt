package com.example.myadventure.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
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
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val processedMessages = remember { mutableSetOf<String>() }
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
                if (message != null && processedMessages.add(message.id)) {
                    messages.add(message)
                    message.imageUrl?.let {
                        if (!imageUrls.contains(it)) {
                            imageUrls.add(it)
                        }
                    }
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
            selectedImageUri = uri // 선택된 이미지 URI 저장
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                        .height(200.dp)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(imageUrls) { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Chat Image",
                            modifier = Modifier
                                .size(180.dp)
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
                            if (message.message.isNotBlank()) {
                                Text(
                                    text = message.message,
                                    color = if (isCurrentUser) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // 메시지 입력 및 전송
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color(0xFFFCE4EC))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = inputMessage,
                    onValueChange = { inputMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp) // 입력창 높이 조정
                        .background(
                            color = Color.White,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp), // 패딩 조정
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp, // 입력 텍스트 크기
                        color = Color.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start // 왼쪽 정렬
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 8.dp), // 기본 텍스트 여백
                            contentAlignment = Alignment.CenterStart // 중앙 왼쪽 정렬
                        ) {
                            if (inputMessage.isEmpty()) {
                                Text(
                                    text = "오늘의 추억을 입력하세요...",
                                    color = Color.Gray,
                                    fontSize = 14.sp, // 기본 텍스트 크기
                                    modifier = Modifier.align(Alignment.CenterStart) // 중앙 왼쪽 정렬
                                )
                            }
                            innerTextField() // 실제 입력 필드
                        }
                    }
                )




                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { imagePickerLauncher.launch("image/*") } // 클릭 시 이미지 선택
                        .background(Color.Transparent) // 배경 투명
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_image),
                        contentDescription = "Image Picker Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            if (selectedImageUri != null) {
                                val imageRef = storage.child("chatRooms/$roomId/${System.currentTimeMillis()}.jpg")
                                val uploadTask = imageRef.putFile(selectedImageUri!!)

                                uploadTask.addOnSuccessListener {
                                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        val imageUrl = downloadUri.toString()

                                        // 새로운 메시지 생성
                                        val newMessage = ChatMessage(
                                            id = database.push().key ?: "",
                                            senderId = currentUser,
                                            imageUrl = imageUrl
                                        )

                                        // 메시지 저장
                                        database.child("chatRooms")
                                            .child(roomId)
                                            .child("messages")
                                            .child(newMessage.id)
                                            .setValue(newMessage)

                                        // 이미지 URL을 chatRooms/roomId/imageUrl에 추가
                                        database.child("chatRooms")
                                            .child(roomId)
                                            .child("imageUrl")
                                            .get()
                                            .addOnSuccessListener { snapshot ->
                                                val urls = snapshot.children.mapNotNull { it.getValue(String::class.java) }.toMutableList()
                                                urls.add(imageUrl) // 새 URL 추가

                                                database.child("chatRooms")
                                                    .child(roomId)
                                                    .child("imageUrl")
                                                    .setValue(urls)
                                                    .addOnSuccessListener {
                                                        selectedImageUri = null // 선택 초기화
                                                        Toast.makeText(context, "이미지 업로드 성공!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "이미지 URL 저장 실패", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                            .addOnFailureListener {
                                                // imageUrl 필드가 비어 있거나 처음 추가하는 경우
                                                database.child("chatRooms")
                                                    .child(roomId)
                                                    .child("imageUrl")
                                                    .setValue(listOf(imageUrl))
                                                    .addOnSuccessListener {
                                                        selectedImageUri = null // 선택 초기화
                                                        Toast.makeText(context, "이미지 업로드 성공!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "이미지 URL 초기 저장 실패", Toast.LENGTH_SHORT).show()
                                                    }
                                            }
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                                }
                            } else if (inputMessage.trim().isNotEmpty()) {
                                val newMessage = ChatMessage(
                                    id = database.push().key ?: "",
                                    senderId = currentUser,
                                    message = inputMessage.trim()
                                )
                                database.child("chatRooms").child(roomId).child("messages")
                                    .child(newMessage.id).setValue(newMessage)
                                inputMessage = ""
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }


            }
        }
    }
}






