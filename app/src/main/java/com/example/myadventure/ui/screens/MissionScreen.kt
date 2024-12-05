package com.example.myadventure.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myadventure.R
import com.example.myadventure.model.Mission

@Composable
fun MissionScreen(
    navController: NavHostController,
    missions: List<Mission>
) {
    var showSelectDialog by remember { mutableStateOf(false) }
    var showSecondDialog by remember { mutableStateOf(false) }
    var selectedMission by remember { mutableStateOf<Mission?>(null) }

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
                MissionSelectionCard(
                    missions = missions,
                    onMissionSelected = { mission ->
                        selectedMission = mission
                        showSelectDialog = true
                    },
                    onRefresh = {}
                )
            }

            if (showSelectDialog && selectedMission != null) {
                AlertDialog(
                    onDismissRequest = { showSelectDialog = false },
                    title = { Text("선택 하시겠습니까?") },
                    text = { Text("미션 '${selectedMission?.title}'을 선택하시겠습니까?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showSelectDialog = false
                            showSecondDialog = true
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

            if (showSecondDialog && selectedMission != null) {
                AlertDialog(
                    onDismissRequest = { showSecondDialog = false },
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = selectedMission?.title ?: "",
                                color = Color(0xFFF776CC),
                                modifier = Modifier.align(Alignment.CenterStart)
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
                        Column(modifier = Modifier.padding(top = 56.dp)) {
                            Text(
                                text = selectedMission?.detail ?: "미션 세부 정보가 없습니다."
                            )
                        }
                    },
                    confirmButton = {
                        Column(modifier = Modifier.padding(top = 32.dp)) {
                            TextButton(onClick = {
                                navController.navigate("find_date_location") {
                                    popUpTo("mission_screen") { inclusive = true }
                                }
                                showSecondDialog = false
                            }) {
                                Text("데이트 장소 찾으러 가기")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                )
            }
        }
    )
}

@Composable
fun MissionSelectionCard(
    missions: List<Mission>,
    onMissionSelected: (Mission) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "은영이와 철구\n" + "D + 85 ❤\n\n", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        missions.forEach { mission ->
            MissionCard(
                mission = mission,
                navController = null,
                onMissionSelected = { onMissionSelected(mission) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MissionCard(
    mission: Mission,
    navController: NavController?,
    onMissionSelected: () -> Unit
) {
    val imageCount = mission.environment / 3 // 환경 점수를 이미지 수로 변환

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onMissionSelected() },
        colors = CardDefaults.cardColors(containerColor = Color(0x59CFC3CE)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(0.0001.dp),
        border = BorderStroke(1.dp, Color(0xFFCFC3CE))
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
