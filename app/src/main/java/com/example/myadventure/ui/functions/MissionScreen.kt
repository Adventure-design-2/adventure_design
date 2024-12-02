package com.example.myadventure.ui.functions

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import kotlinx.serialization.Serializable
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

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
        containerColor = Color(0xFFFFF9F0), // 배경색
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
//                CurrentMissionCard(currentMission ?: "")
                Spacer(modifier = Modifier.height(16.dp))
                MissionSelectionCard(

                    navController = navController,
                    missions = listOf("상남자/상여자 되기", "고요한 저녁 미션!", "실내 활동 미션 1"),

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



@Composable
fun MissionSelectionCard(
    missions: List<String>,
    missionDetails: Map<String, Pair<String, String>>, // 미션별 위치와 설명 정보
    refreshCount: Int,
    remainingTime: Int,
    onMissionSelected: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start // Row 내에서 왼쪽 정렬
    ) {
        IconButton(
            onClick = { /* 버튼 클릭 시 실행할 동작 */ },
            modifier = Modifier
                .size(50.dp) // 버튼 크기
                .padding(bottom = 18.dp) // 미션 선택 텍스트와의 간격 조정
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_currentmisson), // 미션 보관함 아이콘
                contentDescription = "Kakao Icon",
                modifier = Modifier.fillMaxSize(),
                tint = Color.Unspecified // 아이콘 색상을 원본 그대로 사용
            )
        }
    }

        // 미션 선택 텍스트
        Text(text = "미션 선택", style = MaterialTheme.typography.headlineSmall)
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


        // 새로고침 버튼 (주석 처리된 부분은 필요에 따라 활성화)
        // Button(
        //     onClick = onRefresh,
        //     enabled = refreshCount > 0,
        //     modifier = Modifier
        //         .size(50.dp)
        //         .align(Alignment.CenterHorizontally),
        //     colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        // ) {
        //     Icon(
        //         painter = painterResource(id = R.drawable.ic_refresh),
        //         contentDescription = "새로고침",
        //         tint = Color.White
        //     )
        // }
        // Spacer(modifier = Modifier.height(4.dp))
        // if (refreshCount < 3) {
        //     Text(
        //         text = "남은 새로고침 횟수: $refreshCount (타이머: ${remainingTime / 60}분 ${remainingTime % 60}초)",
        //         style = MaterialTheme.typography.bodyMedium
        //     )
        // }

    }
}


@Composable
fun MissionCard(
    mission: Mission,
    navController: NavController,
    onMissionSelected: () -> Unit

) {
    var showDialog by remember { mutableStateOf(false) } // 다이얼로그 표시 상태

    // 카드 컴포저블
    Card(
        modifier = Modifier
            .fillMaxWidth()

            .height(120.dp) // 세로 크기를 더 키움

            .clickable {
                onMissionSelected(mission) // 미션을 선택하고 다이얼로그를 표시

            }
            .border(1.dp, Color(0xFFC0B38B), shape = RoundedCornerShape(8.dp)) // 모서리가 둥근 테두리 추가
            .padding(vertical = 4.dp),

        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F1E4)), // 부드러운 색상
        shape = RoundedCornerShape(8.dp) // Card 자체 모서리도 둥글게 설정
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center, // 텍스트를 가운데 정렬
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // 텍스트를 가운데 정렬
            Text(
                text = mission,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * 0.8f, // 크기를 1.5배 줄임
                color = Color(0xFF6D4C41),
                modifier = Modifier.weight(1f) // 텍스트를 중앙에 배치하도록 가변 공간 할당
            )

            // 미션이 "상남자/상여자 되기"일 때만 아이콘을 표시
            if (mission == "상남자/상여자 되기") {
                Spacer(modifier = Modifier.width(3.dp)) // 텍스트와 아이콘 사이 간격
                Image(
                    painter = painterResource(id = R.drawable.ic_bush),
                    contentDescription = "Bush Icon",
                    modifier = Modifier
                        .size(86.dp) // 아이콘 크기 조절
                        .clickable { showDialog = true } // 이미지 클릭 시 다이얼로그 표시
                )
            }
        }


        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false }, // 다이얼로그 닫기
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween // 제목과 버튼을 양 끝으로 배치
                    ) {
                        Text(text = "상남자/상여자 되기")
                        IconButton(onClick = { showDialog = false }) {
                            // ic_delete.png 이미지로 버튼을 설정
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete), // ic_delete.png
                                contentDescription = "Delete Icon",
                                modifier = Modifier.size(24.dp), // 아이콘 크기 조정
                                tint = Color.Unspecified // 원본 이미지 색을 그대로 사용
                            )
                        }
                    }
                },
                text = { Text("미션을 통해 1그루의 나무를 심은 것 같은 작은 변화를 만들어보세요!") },
                confirmButton = {},
                modifier = Modifier
                    .size(4000.dp * 8 / 7, 1000.dp * 6 / 10) // 가로를 8/7 비율로 설정하고 세로는 6/10 비율로 설정
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp)) // 둥근 모서리 추가
                    .padding(0.dp), // padding을 0으로 설정하여 테두리 안쪽 간격 제거
                containerColor = Color.White // 다이얼로그 배경을 흰색으로 설정
            )
        }




        Box(
            contentAlignment = Alignment.Center,

            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(

                text = mission,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize * 0.8f, // 크기를 1.5배 줄임
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


    NavigationBar(
        containerColor = Color(0xFFF0DDC1) // 배경 색상 설정
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp) // 아이콘 크기를 1/6로 줄임 (원래 크기에 따라 적절히 조정)
                )
            },
            label = {
                Text(
                    "Home",
                    color = if (selectedItem == "home") Color(0xFFFFFFFF) else Color.Black // 선택된 경우 흰색, 그렇지 않은 경우 검정색
                )
            },
            selected = selectedItem == "home",
            modifier = Modifier
                .background(if (selectedItem == "home") Color(0xFFFFC0CB) else Color.Transparent) // 선택된 아이템의 배경색 설정
                .padding(4.dp), // 배경색 여백 추가
            colors = NavigationBarItemDefaults.colors(

                selectedIconColor = Color(0xFFFFFFFF), // 선택된 아이템의 색상

                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "home"
                navController.navigate("mission_screen")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_garden), contentDescription = "Garden",
                modifier = Modifier.size(24.dp)) },
            label = {
                Text(
                    "Garden",
                    color = if (selectedItem == "garden") Color(0xFFFFC0CB) else Color.Black
                )
            },
            selected = selectedItem == "garden",
            modifier = Modifier
                .background(if (selectedItem == "garden") Color(0xFFFFC0CB) else Color.Transparent)
                .padding(4.dp),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "garden"
                navController.navigate("garden_screen")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_shop), contentDescription = "Shop",
                modifier = Modifier.size(24.dp)) },
            label = {
                Text(
                    "Shop",
                    color = if (selectedItem == "shop") Color(0xFFFFC0CB) else Color.Black
                )
            },
            selected = selectedItem == "shop",
            modifier = Modifier
                .background(if (selectedItem == "shop") Color(0xFFFFC0CB) else Color.Transparent)
                .padding(4.dp),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "shop"
                navController.navigate("shop_screen")
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_other), contentDescription = "Other",
                modifier = Modifier.size(24.dp)) },
            label = {
                Text(
                    "Other",
                    color = if (selectedItem == "other") Color(0xFFFFC0CB) else Color.Black
                )
            },
            selected = selectedItem == "other",
            modifier = Modifier
                .background(if (selectedItem == "other") Color(0xFFFFC0CB) else Color.Transparent)
                .padding(4.dp),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                unselectedIconColor = Color.Black
            ),
            onClick = {
                selectedItem = "other"
                navController.navigate("home_screen")
            }

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

