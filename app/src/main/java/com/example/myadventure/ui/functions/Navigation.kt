package com.example.myadventure.ui.functions

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "mission_screen") {
        composable("mission_screen") { MissionScreen(navController) }

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

//            MissionDetailScreen(
//                missionTitle = missionTitle,
//                location = location,
//                instructions = instructions
//            )
        }
    }
}
