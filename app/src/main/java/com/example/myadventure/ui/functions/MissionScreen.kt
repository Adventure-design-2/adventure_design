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
import com.example.myadventure.R
import com.example.myadventure.backend.data.Mission
import com.example.myadventure.backend.data.requestMissionCreation
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionScreen(navController: NavHostController) {
    var mission by remember { mutableStateOf<Mission?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // 화면이 처음 로드될 때 백엔드에 미션 생성 요청을 보냄
    LaunchedEffect(Unit) {
        mission = requestMissionCreation() // 미션 생성 요청
    }

    Scaffold(
        topBar = {
            MissionTopAppBar(
                navController = navController,
                points = 100, // 예시 포인트 값, 실제 데이터로 변경 가능
                userName = "User Name", // 예시 사용자 이름, 실제 데이터로 변경 가능
                profileImageUri = null // 프로필 이미지 URI를 실제 값으로 변경 가능
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (mission != null) {
                    Text("미션 제목: ${mission!!.title}", style = MaterialTheme.typography.headlineMedium)
                    Text("장소: ${mission!!.location}", style = MaterialTheme.typography.bodyMedium)
                    Text("설명: ${mission!!.description}", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("미션을 생성하려면 버튼을 누르세요.", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    coroutineScope.launch {
                        // 버튼 클릭 시에도 미션 생성 요청 가능
                        mission = requestMissionCreation()
                    }
                }) {
                    Text("미션 생성 요청")
                }
            }
        }
    )
}

@Composable
fun MissionSelectionCard(
    navController: NavController,
    missions: List<Mission>,
    onMissionSelected: (Mission) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = "미션 고르기", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Display each mission
        missions.forEach { mission ->
            MissionCard(
                mission = mission,
                navController = navController,
                onMissionSelected = { onMissionSelected(mission) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Refresh button to regenerate missions
        Button(
            onClick = onRefresh,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("새로고침")
        }
    }
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
                    navController.navigate("profile_screen") // 프로필 텍스트 클릭 시에도 유저 정보 화면으로 이동
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
