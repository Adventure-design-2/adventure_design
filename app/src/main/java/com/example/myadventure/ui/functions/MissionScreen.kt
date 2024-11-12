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
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.R
import com.example.myadventure.backend.data.Mission
import com.example.myadventure.backend.data.requestMissionCreation
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@ExperimentalMaterial3Api
@Composable
fun MissionScreen() {
    var mission by remember { mutableStateOf<Mission?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("미션 생성") }) },
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
                        // 미션 생성 요청
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
    points: Int,
    userName: String,
    profileImageUri: Uri?,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFF2E4DA)),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri ?: R.drawable.ic_profile),
                    contentDescription = "프로필 사진",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                UserInfo(userName = userName, points = points, onProfileClick = onProfileClick)
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
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun CurrentMissionCard(selectedMission: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* 미션 상세 보기 */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDEFEF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "현재 미션: $selectedMission",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF6D4C41)
            )
        }
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
