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

@Composable
fun MissionScreen(navController: NavController,  currentMission: String? = "") {
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
                    missions = listOf("환경 미션 1", "야외 활동 미션 1", "실내 활동 미션 1"),
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
    missions: List<String>,
    refreshCount: Int,
    remainingTime: Int,
    onMissionSelected: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "미션 고르기", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            missions.forEach { mission ->
                Button(
                    onClick = { onMissionSelected(mission) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = mission)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
}
