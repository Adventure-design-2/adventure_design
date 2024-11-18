
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
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
            TopAppBar(
                title = { Text("설정") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFF2E4DA))
            )
        },
        bottomBar = { // 하단바 추가
            BottomNavigationBar(navController = navController)
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp)
            ) {
                SettingOptionCard(iconRes = R.drawable.ic_diary, text = "계정") {
                    // 계정 설정으로 이동
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
                SettingOptionCard(iconRes = R.drawable.ic_diary, text = "보안") {
                    // 보안 설정으로 이동
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
                SettingOptionCard(iconRes = R.drawable.ic_diary, text = "알림") {
                    // 알림 설정으로 이동
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
                SettingOptionCard(iconRes = R.drawable.ic_diary, text = "언어") {
                    // 언어 설정으로 이동
                }
                Divider(color = Color.LightGray, thickness = 1.dp)
                SettingOptionCard(iconRes = R.drawable.ic_security, text = "환경기여도") {
                    // 환경기여도 설정으로 이동
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    points: Int,
    userName: String,
    profileImageUri: Uri?,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit // 뒤로 가기 클릭을 처리할 콜백
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        navigationIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back), // 사용자 정의 아이콘 사용
                    contentDescription = "뒤로 가기",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
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
fun SettingOptionCard(iconRes: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = "더보기",
            tint = Color.Gray
        )
    }
}
