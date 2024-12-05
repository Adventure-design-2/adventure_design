package com.example.myadventure.ui.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myadventure.R
import kotlinx.coroutines.delay

@Composable
fun StartScreen(navController: NavController) {
    // 다음 화면으로 이동하는 조건을 위한 상태 변수
    var navigateToNextScreen by remember { mutableStateOf(false) }

    // 일정 시간 후 다음 화면으로 자동 이동
    LaunchedEffect(Unit) {
//        delay(100) // 2초 딜레이
        navController.navigate("signup_screen") {
            popUpTo("start_screen") { inclusive = true } // 현재 화면을 스택에서 제거

        }
    }

    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "!!!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    // 네비게이션 아이템 리스트 정의
    val items = listOf(
        NavBarItem("Diary", R.drawable.ic_diary, "garden_screen"),
        NavBarItem("Mission", R.drawable.ic_misson, "mission_screen"),
        // NavBarItem("Shop", R.drawable.ic_shop, "shop_screen"),
        NavBarItem("Other", R.drawable.ic_other, "other_screen")
    )

    var selectedItem by remember { mutableStateOf("Home") }

    NavigationBar(containerColor = Color(0xFFFFDDF3)) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(38.dp) // 아이콘 크기를 줄임
                    )
                },
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


// NavBarItem 데이터 클래스 정의
data class NavBarItem(val label: String, val iconRes: Int, val route: String)
