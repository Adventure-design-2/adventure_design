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
    private val missionViewModel: MissionViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAdventureTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "signup_screen") { // 시작 화면을 MainScreen으로 설정
                    composable("MainScreen") {
                        MainScreen(navController = navController)
                    }
                    composable("MissionScreen/{dDayResult}") { backStackEntry ->
                        // dDayResult 값을 NavHost 경로에서 수신
                        val dDayResult = backStackEntry.arguments?.getString("dDayResult") ?: "D+0"
                        MissionScreen(
                            navController = navController,
                            viewModel = missionViewModel,
                            dDayResult = dDayResult
                        )
                    }
                    composable("start_screen") {
                        StartScreen(navController = navController)
                    }
                    composable("home_screen") {
                        HomeScreen(navController = navController)
                    }
                    composable("signup_screen") {
                        SignUpScreen(navController = navController)
                    }
                    composable("verification_screen") {
                        VerificationScreen(navController = navController)
                    }
                    composable("CouplecodeScreen") {
                        CouplecodeScreen(navController = navController)
                    }
                    composable("DDayScreen") {
                        DDayScreen(navController = navController)
                    }
                }
            }
        }
    }
}
