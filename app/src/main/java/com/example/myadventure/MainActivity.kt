package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myadventure.ui.functions.*
import com.example.myadventure.ui.profile.ProfileComposable
import com.example.myadventure.ui.theme.MyAdventureTheme

class MainActivity : ComponentActivity() {
    // MissionViewModel을 생성합니다.
    private val missionViewModel: MissionViewModel by viewModels()

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
                        // MissionScreen에 MissionViewModel을 전달합니다.
                        MissionScreen(navController = navController, viewModel = missionViewModel)
                    }
                    composable("profile_screen") {
                        ProfileComposable(navController = navController)
                    }
                    composable("settings_screen") {
                        SettingsScreen(navController = navController)
                    }
                    composable("shop_screen") {
                        ShopScreen(navController = navController)
                    }
//                    composable("diary_screen") {
//                        DiaryScreen(navController = navController)
//                    }
                    composable("signup_screen") {
                        SignUpScreen(navController = navController)
                    }
                    composable("garden_screen") {
                        GardenScreen(navController = navController)
                    }
                    composable("verification_screen") {
                        VerificationScreen(navController = navController)
                    }
                    composable(
                        route = "mission_detail/{missionTitle}/{missionDescription}/{missionLocation}",
                        arguments = listOf(
                            navArgument("missionTitle") { type = NavType.StringType },
                            navArgument("missionDescription") { type = NavType.StringType },
                            navArgument("missionLocation") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val missionTitle = backStackEntry.arguments?.getString("missionTitle") ?: ""
                        val missionDescription = backStackEntry.arguments?.getString("missionDescription") ?: ""
                        val missionLocation = backStackEntry.arguments?.getString("missionLocation") ?: ""
                        MissionDetailScreen(
                            navController = navController,
                            missionTitle = missionTitle,
                            missionDescription = missionDescription,
                            missionLocation = missionLocation
                        )
                    }


                }

            }
        }
    }
}
