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

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterial3Api
@Composable
fun MissionScreen(
    navController: NavHostController,
    viewModel: MissionViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // 화면이 처음 로드될 때 미리 정의된 프롬프트로 미션을 생성하도록 함
    LaunchedEffect(Unit) {
        viewModel.createMissions() // 자동으로 미션 생성 요청
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 상태에 따라 UI 표시
        when (uiState) {
            is UiState.Initial -> {
                Text("미션을 생성 중입니다...")
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success -> {
                val missions = (uiState as UiState.Success).missionContents

                // 미션을 카드로 표시
                missions.take(3).forEach { mission ->
                    MissionCard(
                        mission = mission,
                        navController = navController,
                        onMissionSelected = {
                            // 미션 카드 클릭 시 추가 동작
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            is UiState.Error -> {
                Text("오류: ${(uiState as UiState.Error).message}")
                Button(onClick = {
                    viewModel.createMissions() // 오류 시에도 미리 준비된 프롬프트로 다시 시도
                }) {
                    Text("다시 시도")
                }
            }
            else -> {}
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
