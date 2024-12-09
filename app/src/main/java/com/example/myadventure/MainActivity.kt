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
import com.example.myadventure.ui.screens.*
import com.example.myadventure.ui.theme.MyAdventureTheme

class MainActivity : ComponentActivity() {
    // MissionViewModel 인스턴스를 생성
    private val missionViewModel: MissionViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAdventureTheme {
                // 네비게이션 컨트롤러 생성
                val navController = rememberNavController()

                // 네비게이션 호스트 설정
                NavHost(navController = navController, startDestination = "signup_Screen") { // 초기 화면 설정
                    // DDayScreen 컴포저블 추가
                    composable("DDayScreen") {
                        DDayScreen(navController = navController)
                    }

                    // MainScreen 컴포저블 추가 (dDayResult를 경로 매개변수로 전달받음)
                    composable(
                        route = "MainScreen/{dDayResult}",
                        arguments = listOf(navArgument("dDayResult") { type = NavType.StringType }) // dDayResult를 String 타입으로 정의
                    ) { backStackEntry ->
                        // 경로에서 dDayResult 값을 가져옴
                        val dDayResult = backStackEntry.arguments?.getString("dDayResult") ?: "D+0"
                        MainScreen(navController = navController, dDayResult = dDayResult)
                    }

                    // MissionScreen 컴포저블 추가 (dDayResult를 경로 매개변수로 전달받음)
                    composable(
                        route = "MissionScreen/{dDayResult}",
                        arguments = listOf(navArgument("dDayResult") { type = NavType.StringType }) // dDayResult를 String 타입으로 정의
                    ) { backStackEntry ->
                        // 경로에서 dDayResult 값을 가져옴
                        val dDayResult = backStackEntry.arguments?.getString("dDayResult") ?: "D+0"
                        MissionScreen(
                            navController = navController,
                            viewModel = missionViewModel,
                            dDayResult = dDayResult
                        )
                    }

                    // 기타 화면 컴포저블 추가
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
                }
            }
        }
    }
}
