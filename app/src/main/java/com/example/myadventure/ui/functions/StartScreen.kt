package com.example.myadventure.ui.functions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myadventure.R

@Composable
fun StartScreen(navController: NavController) {
    // 다음 화면으로 이동하는 조건을 위한 상태 변수
    var navigateToNextScreen by remember { mutableStateOf(false) }

    // 일정 시간 후 다음 화면으로 자동 이동
    LaunchedEffect(Unit) {

        delay(2000) // 2초 대기 후
        navigateToNextScreen = true
    }

    // navigateToNextScreen이 true일 때 signup_screen으로 이동
    LaunchedEffect(navigateToNextScreen) {
        if (navigateToNextScreen) {
            navController.navigate("signup_screen") {
                popUpTo("start_screen") { inclusive = true } // 현재 화면을 스택에서 제거
            }

        }
    }

    Scaffold(
        containerColor = Color(0xFFFFF5F8),
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
