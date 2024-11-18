package com.example.myadventure.ui.functions

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.example.myadventure.MissionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@ExperimentalMaterial3Api
@Composable
fun AppNavHost(
    navController: NavHostController,
    missionViewModel: MissionViewModel = viewModel() // MissionViewModel을 기본적으로 가져옴
) {
    NavHost(navController = navController, startDestination = "mission_screen") {
        // MissionScreen 호출 시 navController와 missionViewModel을 전달
        composable("mission_screen") {
            MissionScreen(navController = navController, viewModel = missionViewModel)
        }

        // MissionDetailScreen 호출 부분 수정 - 필요한 매개변수를 추가로 전달
        composable(
            "mission_detail/{missionTitle}/{location}/{instructions}",
            arguments = listOf(
                navArgument("missionTitle") { type = NavType.StringType },
                navArgument("location") { type = NavType.StringType },
                navArgument("instructions") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val missionTitle = backStackEntry.arguments?.getString("missionTitle") ?: "미션"
            val location = backStackEntry.arguments?.getString("location") ?: "알 수 없음"
            val instructions = backStackEntry.arguments?.getString("instructions") ?: "세부 정보가 없습니다."

            // MissionDetailScreen에 필요한 파라미터들을 전달합니다.
            MissionDetailScreen(
                navController = navController,
                missionTitle = missionTitle,
                location = location,
                instructions = instructions
            )
        }
    }
}

@Composable
fun MissionDetailScreen(
    navController: NavHostController,
    missionTitle: String,
    location: String,
    instructions: String
) {
    // MissionDetailScreen에서 전달받은 데이터를 이용하여 화면을 구성합니다.
    Column {
        Text(text = "미션 제목: $missionTitle")
        Text(text = "위치: $location")
        Text(text = "설명: $instructions")

        // 뒤로가기 버튼 예시
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "뒤로 가기")
        }
    }
}
