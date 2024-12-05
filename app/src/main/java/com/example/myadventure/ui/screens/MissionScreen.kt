package com.example.myadventure.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.myadventure.Mission
import com.example.myadventure.MissionViewModel
import com.example.myadventure.R
import kotlinx.serialization.Serializable

@Composable
fun MissionScreen(
    navController: NavHostController,
    viewModel: MissionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var showSelectDialog by remember { mutableStateOf(false) }
    var showSecondDialog by remember { mutableStateOf(false) }
    var selectedMission by remember { mutableStateOf<String?>(null) }

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
                    missions = listOf("상남자/상여자 되기", "고요한 저녁 미션!", "실내 활동 미션 1"),
                    missionDetails = mapOf(
                        "상남자/상여자 되기" to ("공원" to "쓰레기 줍기"),
                        "고요한 저녁 미션!" to ("산" to "산책하기"),
                        "실내 활동 미션 1" to ("집" to "정리정돈")
                    ),
                    refreshCount = 3,
                    remainingTime = 300,
                    onMissionSelected = { mission ->
                        selectedMission = mission
                        showSelectDialog = true
                    },
                    onRefresh = {}
                )
            }

            if (showSelectDialog) {
                AlertDialog(
                    onDismissRequest = { showSelectDialog = false },
                    title = { Text("선택 하시겠습니까?") },
                    text = { Text("이 미션을 선택하시겠습니까?") },
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

            if (showSecondDialog) {
                AlertDialog(
                    onDismissRequest = { showSecondDialog = false },
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(48.dp))
                            Text(
                                "상남자/상여자 되기 미션!",
                                color = Color(0xFFF776CC)
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
                                "오늘은 컵만 들고 음료를 마셔보세요!" + "\n" +
                                        "빨대 없이 진지하게 한 모금, \n" +
                                        "연인과 웃음이 가득한 특별한 순간이 될지도? \n\n" +
                                        "가볍게 도전하며 즐거운 \n" +
                                        "데이트를 만들어보세요!"
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

@Serializable
data class Mission(
    val title: String,
    val location: String,
    val description: String
)

@Composable
fun MissionSelectionCard(
    missions: List<String>,
    missionDetails: Map<String, Pair<String, String>>,
    refreshCount: Int,
    remainingTime: Int,
    onMissionSelected: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "은영이와 철구\n" + "D + 85 ❤\n\n", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        missions.forEach { mission ->
            MissionCard(
                mission = Mission(
                    title = mission,
                    location = missionDetails[mission]?.first ?: "",
                    description = missionDetails[mission]?.second ?: ""
                ),
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
    val imageCount = when (mission.title) {
        "상남자/상여자 되기" -> 2
        "고요한 저녁 미션!" -> 1
        "실내 활동 미션 1" -> 3
        else -> 0
    }

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

@Preview
@Composable
fun MissionScreen(){
    MissionScreen(navController = NavController(LocalContext.current))
}