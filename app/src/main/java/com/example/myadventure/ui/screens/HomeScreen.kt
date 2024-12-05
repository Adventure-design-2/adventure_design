package com.example.myadventure.ui.screens

// Compose와 Android 관련 import
import android.net.Uri
import androidx.compose.foundation.Image
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
import com.example.myadventure.ui.screens.card.Feature
import com.example.myadventure.ui.screens.card.FeatureCardRow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current


    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    // 설정 아이콘 버튼을 오른쪽 상단에 배치
                    IconButton(onClick = { navController.navigate("settings_screen") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_setting),
                            contentDescription = "설정",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFF2E4DA))
            )
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // 주요 기능 카드 표시
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FeatureCardRow(
                        navController = navController,
                        firstFeature = Feature("mission_detail/산책하기", "미션", R.drawable.ic_mission),
                        secondFeature = Feature("diary_screen", "다이어리", R.drawable.ic_diary)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureCardRow(
                        navController = navController,
                        firstFeature = Feature("garden_screen", "정원", R.drawable.ic_garden),
                        secondFeature = Feature("map_screen", "지도", R.drawable.ic_map)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(100.dp)
                            .offset(x = 16.dp, y = (-16).dp),
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
            }
        }
    )
}

@Composable
fun UserProfileSection(userName: String, profileImageUri: Uri?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = profileImageUri?.let { rememberAsyncImagePainter(it) }
                ?: painterResource(id = R.drawable.ic_profile),
            contentDescription = "프로필 사진",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .padding(end = 16.dp)
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
