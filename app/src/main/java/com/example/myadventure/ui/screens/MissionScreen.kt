package com.example.myadventure.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.myadventure.R
import com.example.myadventure.data.ChatRoomRepository
import com.example.myadventure.data.MissionRepository
import com.example.myadventure.model.Mission
import com.example.myadventure.model.UserProfile
import com.example.myadventure.viewmodel.AuthViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@Composable
fun MissionScreen(
    navController: NavHostController,
    repository: MissionRepository,
    dDayResult: String // D-Day 값을 전달받음
) {
    val recommendedMissions = remember { repository.getRecommendedMissions() }
    var showSelectDialog by remember { mutableStateOf(false) }
    var showSecondDialog by remember { mutableStateOf(false) }
    var selectedMission by remember { mutableStateOf<Mission?>(null) }
    val context = LocalContext.current
    val userProfile = remember { mutableStateOf<UserProfile?>(null) }
    val authViewModel = AuthViewModel()
    val coroutineScope = rememberCoroutineScope()
    var newlyCreatedChatRoomId by remember { mutableStateOf<String?>(null) } // 상대방이 생성한 채팅방 ID
// 이미지 전환을 위한 상태



    // 현재 사용자 정보 로드 및 상대방이 생성한 채팅방 감지
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            authViewModel.loadUserProfile { profile ->
                userProfile.value = profile

                val currentUser = profile?.uid
                if (!currentUser.isNullOrEmpty()) {
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("chatRooms")
                        .orderByChild("user2")
                        .equalTo(currentUser)
                        .addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                val roomId = snapshot.key
                                if (!roomId.isNullOrEmpty()) {
                                    newlyCreatedChatRoomId = roomId
                                }
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {}

                            override fun onChildRemoved(snapshot: DataSnapshot) {}
                            override fun onCancelled(error: DatabaseError) {}
                            override fun onChildMoved(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {}
                        })
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0x5EFFC1E3),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "은영이와 철구\n $dDayResult ❤\n\n",
                    style = MaterialTheme.typography.headlineSmall
                )

                // 상대방이 생성한 채팅방으로 이동 버튼
                newlyCreatedChatRoomId?.let { roomId ->
                    Button(
                        onClick = {
                            navController.navigate("chat_room_screen/$roomId") {
                                popUpTo("mission_screen") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("새로운 채팅방으로 이동")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 미션 선택 카드
                MissionSelectionCard(
                    missions = recommendedMissions,
                    onMissionSelected = { mission ->
                        selectedMission = mission
                        showSelectDialog = true
                    }
                )
            }

            // 미션 선택 확인 Dialog
            if (showSelectDialog) {
                AlertDialog(
                    onDismissRequest = { showSelectDialog = false },
                    title = { Text("선택 하시겠습니까?") },
                    text = { Text("이 미션을 선택하시겠습니까?") },
                    confirmButton = {
                        TextButton(onClick = {
                            val user1 = userProfile.value?.uid ?: ""
                            val user2 = userProfile.value?.partnerUid ?: ""
                            val mission = selectedMission

                            if (user1.isNotBlank() && user2.isNotBlank() && mission != null) {
                                val chatRoomRepository = ChatRoomRepository()
                                chatRoomRepository.createChatRoom(user1, user2, mission) { roomId ->
                                    if (roomId != null) {
                                        newlyCreatedChatRoomId = roomId
                                        Toast.makeText(context, "기록 채팅방 생성 완료!", Toast.LENGTH_SHORT)
                                            .show()
                                        showSecondDialog = true // AlertDialog를 수동으로 관리
                                    } else {
                                        Toast.makeText(context, "기록 채팅방 생성 실패.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, "사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            showSelectDialog = false
                        }) {
                            Text("예")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSelectDialog = false }) {
                            Text("아니요")
                        }
                    }
                )
            }

            // 두 번째 AlertDialog
            if (showSecondDialog && newlyCreatedChatRoomId != null) {
                AlertDialog(
                    onDismissRequest = { showSecondDialog = false },
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = selectedMission?.title ?: "미션 제목 없음",
                                color = Color(0xFFF776CC),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(16.dp)
                            )
                            IconButton(
                                onClick = { showSecondDialog = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_delete),
                                    contentDescription = "닫기",
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    },
                    text = {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = selectedMission?.detail ?: "미션 세부 정보가 없습니다.",
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            // WebView 추가
                            AndroidView(
                                factory = { context ->
                                    WebView(context).apply {
                                        webViewClient = WebViewClient()
                                        settings.javaScriptEnabled = true
                                        val locationQuery = selectedMission?.locationTag ?: "서울"
                                        val naverMapUrl = "https://m.map.naver.com/search2/search.naver?query=$locationQuery&sm=hty&style=v5#/map/1"
                                        loadUrl(naverMapUrl)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                            )

                        }
                    },
                    confirmButton = {
                        Column(modifier = Modifier.padding(16.dp)) {
                            TextButton(onClick = {
                                navController.navigate("chat_room_screen/${newlyCreatedChatRoomId}") {
                                    popUpTo("mission_screen") { inclusive = true }
                                }
                                showSecondDialog = false
                            }) {
                                Text("채팅방으로 이동")
                            }
                        }
                    }
                )
            }
        }
    )
}



@Composable
fun MissionSelectionCard(
    missions: List<Mission>,
    onMissionSelected: (Mission) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "추천 미션",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        missions.forEach { mission ->
            MissionCard(
                mission = mission,
                onMissionSelected = { onMissionSelected(mission) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MissionCard(
    mission: Mission,
    onMissionSelected: () -> Unit
) {
    val imageCount = mission.environment

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onMissionSelected() },
        colors = CardDefaults.cardColors(containerColor = Color(0x59CFC3CE)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(0.0001.dp), // 옅은 그림자 효과
        border = BorderStroke(1.dp, Color(0xFFCFC3CE)) // 테두리 추가
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mission.title,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * 0.8f,
                color = Color(0xFF6D4C41),
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                repeat(imageCount) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bush),
                        contentDescription = "Bush Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
