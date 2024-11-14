package com.example.myadventure.ui.functions

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.Mission
import com.example.myadventure.MissionViewModel
import com.example.myadventure.R
import com.example.myadventure.UiState
import kotlinx.serialization.Serializable
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun MissionScreen(
    navController: NavHostController,
    viewModel: MissionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()


    // 미션 선택 다이얼로그 상태와 선택된 미션 상태 추가
    var showSelectDialog by remember { mutableStateOf(false) }
    var selectedMission by remember { mutableStateOf<String?>(null) } // 실제 선택된 미션 저장

    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        topBar = {
            MissionTopAppBar(
                points = points,
                userName = userName,
                profileImageUri = profileImageUri,
                onProfileClick = { navController.navigate("profile_screen") }
            )
        },
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
                CurrentMissionCard(currentMission ?: "")
                Spacer(modifier = Modifier.height(16.dp))
                MissionSelectionCard(
                    missions = listOf("상남자/상여자 되기", "시간여행하기", "실내 활동 미션 1"),
                    missionDetails = mapOf(
                        "상남자/상여자 되기" to ("공원" to "쓰레기 줍기"),
                        "야외 활동 미션 1" to ("산" to "산책하기"),
                        "실내 활동 미션 1" to ("집" to "정리정돈")
                    ),
                    refreshCount = 3,
                    remainingTime = 300,
                    onMissionSelected = { mission ->
                        selectedMission = mission // 미션을 선택하고 저장
                        showSelectDialog = true // 다이얼로그 표시
                    },
                    onRefresh = {}
                )
            }

            // AlertDialog 추가
            if (showSelectDialog) {
                AlertDialog(
                    onDismissRequest = { showSelectDialog = false },
                    title = { Text("선택 하시겠습니까?") },
                    text = { Text("이 미션을 선택하시겠습니까?") },
                    confirmButton = {
                        TextButton(onClick = {
                            // "예" 버튼을 눌렀을 때만 다음 화면으로 이동
                            selectedMission?.let {
                                val encodedMissionTitle = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                                navController.navigate("mission_detail/$encodedMissionTitle") {
                                    popUpTo("mission_screen") { inclusive = true }
                                }
                            }
                            showSelectDialog = false // 다이얼로그 닫기
                        }) {
                            Text("예")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showSelectDialog = false // 다이얼로그 닫기
                        }) {
                            Text("아니요")
                        }
                    }
                )
            }

        }
    }
}

@Serializable
data class Mission(
    val title: String,
    val location: String,
    val description: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionTopAppBar(
    navController: NavController,
    points: Int,
    userName: String,
    profileImageUri: Uri?
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFF2E4DA)),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 프로필 이미지 클릭 시 유저 정보 화면으로 이동
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri ?: R.drawable.ic_profile),
                    contentDescription = "프로필 사진",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            navController.navigate("profile_screen")
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))

                // 유저 정보 표시
                UserInfo(userName = userName, points = points) {
                    navController.navigate("profile_screen")
                }
            }
        }
    )
}

@Composable
fun UserInfo(userName: String, points: Int, onProfileClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = userName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clickable { onProfileClick() }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "포인트: $points",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )

//        {
//            Text(text = "현재 미션: $selectedMission", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF6D4C41))
//        }
    }
}

@Composable
fun MissionSelectionCard(
    missions: List<String>,
    missionDetails: Map<String, Pair<String, String>>, // 미션별 위치와 설명 정보
    refreshCount: Int,
    remainingTime: Int,
    onMissionSelected: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "미션 고르기", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 각 미션을 Card로 감싸서 표시
        missions.forEach { mission ->
            MissionCard(
                mission = mission,
                onMissionSelected = onMissionSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

//        // 새로고침 버튼
//        Button(
//            onClick = onRefresh,
//            enabled = refreshCount > 0,
//            modifier = Modifier
//                .size(50.dp)
//                .align(Alignment.CenterHorizontally),
//            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_refresh),
//                contentDescription = "새로고침",
//                tint = Color.White
//            )
//        }
//        Spacer(modifier = Modifier.height(4.dp))
//        if (refreshCount < 3) {
//            Text(
//                text = "남은 새로고침 횟수: $refreshCount (타이머: ${remainingTime / 60}분 ${remainingTime % 60}초)",
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }




    }
}

@Composable
fun MissionCard(
    mission: Mission,
    navController: NavController,
    onMissionSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                onMissionSelected()
                val encodedMissionTitle = URLEncoder.encode(mission.title, StandardCharsets.UTF_8.toString())
                navController.navigate("mission_detail/$encodedMissionTitle")
            }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7E7E7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = mission.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6D4C41)
            )
            Text(
                text = "${mission.location} - ${mission.description}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf("home") }

    NavigationBar(containerColor = Color(0xFFEAD9C9)) {
        val items = listOf(
            NavBarItem("Home", R.drawable.ic_home, "mission_screen"),
            NavBarItem("Garden", R.drawable.ic_garden, "garden_screen"),
            NavBarItem("Shop", R.drawable.ic_shop, "shop_screen"),
            NavBarItem("Other", R.drawable.ic_other, "home_screen")
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.iconRes), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == item.label,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFC0CB),
                    unselectedIconColor = Color.Black
                ),
                onClick = {
                    selectedItem = item.label
                    navController.navigate(item.route)
                }
            )
        }
    }
}

data class NavBarItem(val label: String, val iconRes: Int, val route: String)
