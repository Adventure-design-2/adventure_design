package com.example.myadventure.ui.functions

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionScreen(
    navController: NavHostController,
    viewModel: MissionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // 미션 선택 다이얼로그 상태와 선택된 미션 상태 추가
    var showMissionDetailDialog by remember { mutableStateOf(false) }
    var selectedMission by remember { mutableStateOf<Mission?>(null) }

    Scaffold(
        containerColor = Color(0xFFFFF9F0), // 배경색
        topBar = {
            MissionTopAppBar(
                navController = navController,
                points = 100, // 예시 값, 실제 사용 시 적절히 교체해야 합니다.
                userName = "사용자 이름", // 예시 값
                profileImageUri = null
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 상태에 따라 UI 표시
                when (uiState) {
                    is UiState.Initial -> {
                        Text("미션을 생성할 준비가 되었습니다.")
                    }
                    is UiState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is UiState.Success -> {
                        val missions = (uiState as UiState.Success).missionContents

                        // 미션을 카드로 표시 (제목만 표시)
                        missions.take(3).forEach { mission ->
                            MissionTitleCard(
                                mission = mission,
                                onMissionSelected = {
                                    selectedMission = mission
                                    showMissionDetailDialog = true
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    is UiState.Error -> {
                        Text("오류: ${(uiState as UiState.Error).message}")
                        Button(
                            onClick = {
                                viewModel.createMissions() // 오류 시에도 다시 시도할 수 있도록 버튼 추가
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)),
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            Text("다시 시도")
                        }
                    }
                }

                // 미션 생성하기 버튼 추가 (미션 카드보다 아래 위치)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        viewModel.createMissions() // 새 미션 생성 요청
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text("새 미션 생성하기")
                }

                // 선택된 미션의 세부 사항을 보여주는 AlertDialog
                if (showMissionDetailDialog && selectedMission != null) {
                    AlertDialog(
                        onDismissRequest = { showMissionDetailDialog = false },
                        title = { Text(selectedMission?.title ?: "") },
                        text = {
                            Column {
                                Text("장소: ${selectedMission?.location ?: "정보 없음"}")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("설명: ${selectedMission?.description ?: "정보 없음"}")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showMissionDetailDialog = false }) {
                                Text("확인")
                            }
                        }
                    )
                }
            }
        }
    )
}

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
    }
}

@Composable
fun MissionTitleCard(
    mission: Mission,
    onMissionSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onMissionSelected() } // 함수에 인자 없이 호출
            .border(1.dp, Color(0xFFC0B38B), shape = RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F1E4)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = mission.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6D4C41)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf("Home") }

    val items = listOf(
        NavBarItem("Home", R.drawable.ic_home, "mission_screen"),
        NavBarItem("Garden", R.drawable.ic_garden, "garden_screen"),
        NavBarItem("Shop", R.drawable.ic_shop, "shop_screen"),
        NavBarItem("Other", R.drawable.ic_other, "home_screen")
    )

    NavigationBar(containerColor = Color(0xFFEAD9C9)) {
        items.forEach { item: NavBarItem ->
            NavigationBarItem(
                icon = {
                    Icon(painterResource(id = item.iconRes), contentDescription = item.label)
                },
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
