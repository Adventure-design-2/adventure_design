@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.ui.functions.*
import com.example.myadventure.ui.profile.ProfileComposable
import com.example.myadventure.ui.theme.MyAdventureTheme
//import com.example.myadventure.ui.auth.SignUpScreen

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
                    composable("settings_screen") {
                        SettingsScreen(navController = navController) // 설정 화면 추가
                    }
                    //composable("map_screen") {
                        //MapScreen() // 지도 화면 추가
                    //}
                    composable("shop_screen") {
                        ShopScreen(navController = navController) // 설정 화면 추가
                    }
                    composable("signup_screen") {
                        SignUpScreen(navController = navController)// 설정 화면 추가
                    }
                    composable("garden_screen") {
                        GardenScreen(navController = navController)// 설정 화면 추가
                    }
                    composable("verification_screen") {
                        VerificationScreen(navController = navController)// 설정 화면 추가
                    }
                    composable(
                        route = "mission_detail/{missionTitle}",
                        arguments = listOf(
                            navArgument("missionTitle") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val missionTitle = backStackEntry.arguments?.getString("missionTitle") ?: ""
                        MissionDetailScreen(navController = navController,missionTitle = missionTitle)
                    }
                }
            }
        }
    }
}