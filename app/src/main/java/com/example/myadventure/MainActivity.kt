package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.ui.functions.*
import com.example.myadventure.ui.profile.ProfileComposable
import com.example.myadventure.ui.theme.MyAdventureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAdventureTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "start_screen") {
                    composable("start_screen") {
                        StartScreen(navController = navController)
                    }
                    composable("home_screen") {
                        HomeScreen(navController = navController)
                    }
                    composable("mission_screen") {
                        MissionScreen(navController = navController)
                    }
                    composable("profile_screen") {
                        ProfileComposable(navController = navController)
                    }
                    composable(
                        route = "mission_detail/{missionTitle}/{location}/{instructions}",
                        arguments = listOf(
                            navArgument("missionTitle") { defaultValue = "" },
                            navArgument("location") { defaultValue = "" },
                            navArgument("instructions") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val missionTitle = backStackEntry.arguments?.getString("missionTitle") ?: ""
                        val location = backStackEntry.arguments?.getString("location") ?: ""
                        val instructions = backStackEntry.arguments?.getString("instructions") ?: ""
                        MissionDetailScreen(missionTitle, location, instructions)
                    }
                }
            }
        }
    }
}
