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
        topBar = {
            MissionTopAppBar(
                points = points,
                userName = userName,
                profileImageUri = profileImageUri,
                onProfileClick = { navController.navigate("profile_screen") }
            )
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
                    navController = navController, // 추가된 navController 전달
                    missions = listOf("환경 미션 1", "야외 활동 미션 1", "실내 활동 미션 1"),
                    missionDetails = mapOf(
                        "환경 미션 1" to ("공원" to "쓰레기 줍기"),
                        "야외 활동 미션 1" to ("산" to "산책하기"),
                        "실내 활동 미션 1" to ("집" to "정리정돈")
                    ),
                    refreshCount = 3,
                    remainingTime = 300,
                    onMissionSelected = {}, // 필요한 경우 이 콜백을 정의
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
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 프로필 이미지
                Image(
                    painter = profileImageUri?.let { rememberAsyncImagePainter(it) }
                        ?: painterResource(id = R.drawable.ic_profile),
                    contentDescription = "프로필 사진",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 사용자 이름을 클릭 가능한 텍스트로 표시
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
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "현재 미션: $selectedMission", style = MaterialTheme.typography.headlineSmall)
        }
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
            val (location, instructions) = missionDetails[mission] ?: ("알 수 없음" to "세부 정보가 없습니다.")
            MissionCard(
                mission = mission,
                location = location,
                instructions = instructions,
                navController = navController,
                onMissionSelected = onMissionSelected
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 새로고침 버튼
        Button(
            onClick = onRefresh,
            enabled = refreshCount > 0,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "새로고침",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (refreshCount < 3) {
            Text(
                text = "남은 새로고침 횟수: $refreshCount (타이머: ${remainingTime / 60}분 ${remainingTime % 60}초)",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MissionCard(
    mission: String,
    location: String,
    instructions: String,
    navController: NavController,
    onMissionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onMissionSelected(mission)

                // 미션 세부 정보 인코딩
                val encodedMissionTitle = URLEncoder.encode(mission, StandardCharsets.UTF_8.toString())
                val encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString())
                val encodedInstructions = URLEncoder.encode(instructions, StandardCharsets.UTF_8.toString())

                // 인코딩된 문자열을 경로로 전달하여 상세 화면으로 이동
                navController.navigate("mission_detail/$encodedMissionTitle/$encodedLocation/$encodedInstructions")
            }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)) // 예쁜 배경색 적용
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = mission, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "위치: $location", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "설명: $instructions", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
