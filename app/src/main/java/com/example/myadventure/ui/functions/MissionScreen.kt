package com.example.myadventure.ui.functions

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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

// <<<<<<< main


// @Composable
// fun MissionSelectionCard(
//     missions: List<String>,
//     missionDetails: Map<String, Pair<String, String>>, // 미션별 위치와 설명 정보
//     refreshCount: Int,
//     remainingTime: Int,
//     onMissionSelected: (String) -> Unit,
//     onRefresh: () -> Unit
// ) {
//     Row(
//         modifier = Modifier.fillMaxWidth(),
//         horizontalArrangement = Arrangement.Start // Row 내에서 왼쪽 정렬
//     ) {
//         IconButton(
//             onClick = { /* 버튼 클릭 시 실행할 동작 */ },
//             modifier = Modifier
//                 .size(50.dp) // 버튼 크기
//                 .padding(bottom = 18.dp) // 미션 선택 텍스트와의 간격 조정
//         ) {
//             Icon(
//                 painter = painterResource(id = R.drawable.ic_currentmisson), // 미션 보관함 아이콘
//                 contentDescription = "Kakao Icon",
//                 modifier = Modifier.fillMaxSize(),
//                 tint = Color.Unspecified // 아이콘 색상을 원본 그대로 사용
//             )
//         }
//     }

//         // 미션 선택 텍스트
//         Text(text = "미션 선택", style = MaterialTheme.typography.headlineSmall)
//         Spacer(modifier = Modifier.height(16.dp))

//         // 각 미션을 Card로 감싸서 표시
//         missions.forEach { mission ->
//             MissionCard(
//                 mission = mission,
//                 onMissionSelected = onMissionSelected
//             )
//             Spacer(modifier = Modifier.height(16.dp))
//         }
//     }
// }


//     }


// =======
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
fun MissionTitleCard(
    mission: Mission,
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
// =======
//             .height(100.dp)
//             .clickable { onMissionSelected() }
// >>>>>>> seung
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
// <<<<<<< main
//             // 텍스트를 가운데 정렬
//             Text(
//                 text = mission,
//                 fontSize = MaterialTheme.typography.headlineMedium.fontSize * 0.8f, // 크기를 1.5배 줄임
//                 color = Color(0xFF6D4C41),
//                 modifier = Modifier.weight(1f) // 텍스트를 중앙에 배치하도록 가변 공간 할당
//             )

//             // 미션이 "상남자/상여자 되기"일 때만 아이콘을 표시
//             if (mission == "상남자/상여자 되기") {
//                 Spacer(modifier = Modifier.width(3.dp)) // 텍스트와 아이콘 사이 간격
//                 Image(
//                     painter = painterResource(id = R.drawable.ic_bush),
//                     contentDescription = "Bush Icon",
//                     modifier = Modifier
//                         .size(86.dp) // 아이콘 크기 조절
//                         .clickable { showDialog = true } // 이미지 클릭 시 다이얼로그 표시
//                 )
//             }
//         }


//         if (showDialog) {
//             AlertDialog(
//                 onDismissRequest = { showDialog = false }, // 다이얼로그 닫기
//                 title = {
//                     Row(
//                         modifier = Modifier.fillMaxWidth(),
//                         horizontalArrangement = Arrangement.SpaceBetween // 제목과 버튼을 양 끝으로 배치
//                     ) {
//                         Text(text = "상남자/상여자 되기")
//                         IconButton(onClick = { showDialog = false }) {
//                             // ic_delete.png 이미지로 버튼을 설정
//                             Icon(
//                                 painter = painterResource(id = R.drawable.ic_delete), // ic_delete.png
//                                 contentDescription = "Delete Icon",
//                                 modifier = Modifier.size(24.dp), // 아이콘 크기 조정
//                                 tint = Color.Unspecified // 원본 이미지 색을 그대로 사용
//                             )
//                         }
//                     }
//                 },
//                 text = { Text("미션을 통해 1그루의 나무를 심은 것 같은 작은 변화를 만들어보세요!") },
//                 confirmButton = {},
//                 modifier = Modifier
//                     .size(4000.dp * 8 / 7, 1000.dp * 6 / 10) // 가로를 8/7 비율로 설정하고 세로는 6/10 비율로 설정
//                     .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp)) // 둥근 모서리 추가
//                     .padding(0.dp), // padding을 0으로 설정하여 테두리 안쪽 간격 제거
//                 containerColor = Color.White // 다이얼로그 배경을 흰색으로 설정
//             )
//         }




//         Box(
//             contentAlignment = Alignment.Center,

//             modifier = Modifier
//                 .fillMaxSize()
//                 .padding(8.dp)
//         ) {
//             Text(

//                 text = mission,
//                 fontSize = MaterialTheme.typography.headlineMedium.fontSize * 0.8f, // 크기를 1.5배 줄임
//                 color = Color(0xFF6D4C41)

//             )
//             Text(
//                 text = "${mission.location} - ${mission.description}",
//                 style = MaterialTheme.typography.bodyMedium,
//                 color = Color.Gray
// =======
            // 미션 제목만 표시
            Text(
                text = mission.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF6D4C41),
                modifier = Modifier.align(Alignment.CenterHorizontally)

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

// =======
//     NavigationBar(containerColor = Color(0xFFEAD9C9)) {
//         val items = listOf(
//             NavBarItem("Home", R.drawable.ic_home, "mission_screen"),
//             NavBarItem("Garden", R.drawable.ic_garden, "garden_screen"),
//             NavBarItem("Shop", R.drawable.ic_shop, "shop_screen"),
//             NavBarItem("Other", R.drawable.ic_other, "home_screen")
// >>>>>>> seung
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
