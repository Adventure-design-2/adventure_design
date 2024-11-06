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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.R
import com.example.myadventure.ui.profile.UserPreferences
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem


@Composable
fun MissionScreen(navController: NavController, currentMission: String? = "") {
    val context = LocalContext.current
    val userPreferences = UserPreferences.getInstance(context)

    // 사용자 포인트, 이름, 프로필 이미지 URI 상태 관리
    val points by userPreferences.pointsFlow.collectAsState(initial = userPreferences.getPoints())
    val userName by remember { mutableStateOf(userPreferences.getUserName()) }
    val profileImageUriString by remember { mutableStateOf(userPreferences.getProfileImageUri()) }
    val profileImageUri = profileImageUriString?.let { Uri.parse(it) }

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

        bottomBar = { // 추가된 부분
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
                    navController = navController,
                    missions = listOf("상남자/상여자 되기", "시간여행하기", "실내 활동 미션 1"),
                    missionDetails = mapOf(
                        "상남자/상여자 되기" to ("공원" to "쓰레기 줍기"),
                        "야외 활동 미션 1" to ("산" to "산책하기"),
                        "실내 활동 미션 1" to ("집" to "정리정돈")
                    ),
                    refreshCount = 3,
                    remainingTime = 300,
                    onMissionSelected = {},
                    onRefresh = {}
                )
            }
        }
    )
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
                // 프로필 이미지 (URI가 null일 경우 기본 이미지 사용)
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri ?: R.drawable.ic_profile),
                    contentDescription = "프로필 사진",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 사용자 이름
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.clickable { onProfileClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 포인트 표시
                Text(
                    text = "포인트: $points",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    )
}

@Composable
fun CurrentMissionCard(selectedMission: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* 미션 상세 보기 */ },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDEFEF)) // 밝은 배경 색상 적용
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
//        {
//            Text(text = "현재 미션: $selectedMission", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF6D4C41))
//        }
    }
}

@Composable
fun MissionSelectionCard(
    navController: NavController,
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
                navController = navController,
                onMissionSelected = onMissionSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
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
    mission: String,
    navController: NavController,
    onMissionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // 세로 크기를 더 키움
            .clickable {
                onMissionSelected(mission)

                // 미션 제목만 전달하여 상세 화면으로 이동
                val encodedMissionTitle = URLEncoder.encode(mission, StandardCharsets.UTF_8.toString())
                navController.navigate("mission_detail/$encodedMissionTitle")
            }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7E7E7)) // 부드러운 색상
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = mission,
                style = MaterialTheme.typography.headlineMedium, // 더 큰 폰트로 제목을 강조
                color = Color(0xFF6D4C41)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf("home") }

    NavigationBar(
        containerColor = Color(0xFFEAD9C9) // 배경 색상 설정
    ) {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_home), contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == "home",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFC0CB), // 선택된 아이템의 색상
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "home"
                navController.navigate("mission_screen") // 'home' 클릭 시 'mission_screen'으로 이동
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_garden), contentDescription = "Garden") },
            label = { Text("Garden") },
            selected = selectedItem == "garden",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFC0CB),
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "garden"
                navController.navigate("garden_screen")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_shop), contentDescription = "Shop") },
            label = { Text("Shop") },
            selected = selectedItem == "shop",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFC0CB),
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "shop"
                navController.navigate("shop_screen")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_other), contentDescription = "Other") },
            label = { Text("Other") },
            selected = selectedItem == "other",
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFC0CB),
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "other"
                navController.navigate("home_screen") // 'other' 클릭 시 'home_screen'으로 이동
            }
        )
    }
}

