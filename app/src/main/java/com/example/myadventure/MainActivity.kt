package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.ui.screens.*
import com.example.myadventure.ui.theme.MyAdventureTheme


class MainActivity : ComponentActivity() {
    // MissionViewModel을 생성합니다.

    @OptIn(ExperimentalMaterial3Api::class)
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
                    composable("Missionscreen") {
                        // MissionScreen에 MissionViewModel을 전달합니다.
                        MissionScreen(navController = navController)
                    }

//                    composable("map_screen") {
//                        MapScreen() // 지도 화면 추가
//                    }

                    composable("signup_screen") {
                        SignUpScreen(navController = navController)
                    }
                    composable("verification_screen") {
                        VerificationScreen(navController = navController)
                    }
                    composable("CouplecodeScreen") {
                        CouplecodeScreen(navController = navController)
                    }
                    composable("MainScreen") {
                        MainScreen(navController = navController)
                    }
                    composable("DDayScreen") {
                        DDayScreen(navController = navController)
                    }

                }
            }
        }
    }
}
