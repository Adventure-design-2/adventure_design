package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.data.MissionRepository
import com.example.myadventure.ui.functions.DiaryScreens
import com.example.myadventure.ui.screens.*
import com.example.myadventure.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController: NavHostController = rememberNavController()
            val authViewModel: AuthViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
            val recordViewModel: RecordViewModel = ViewModelProvider(this, RecordViewModelFactory(applicationContext))[RecordViewModel::class.java]
            val missionRepository = MissionRepository(this)

            // 로그인 상태 확인
            authViewModel.checkAuthState()

            // 네비게이션 호스트 설정
            NavHost(navController, startDestination = "start_screen") {
                composable("start_screen") {
                    StartScreen(navController = navController)
                }
                composable("signup_screen") {
                    SignUpScreen(navController = navController, viewModel = authViewModel)
                }
                composable("main_screen") {
                    MainScreen(navController = navController)
                }
                composable("mission_screen") {
                    MissionScreen(navController = navController, repository = missionRepository)
                }
                composable("record_upload_screen") {
                    RecordUploadScreen(navController = navController, viewModel = recordViewModel)
                }
                composable("diary_screen") {
                    DiaryScreens(navController = navController)
                }
                composable("other_screen") {
                    // 예시 화면, other_screen 추가
                }
            }

            // 로그인 상태에 따라 초기 화면 설정
            val authState = authViewModel.authState.collectAsState().value
            LaunchedEffect(authState) {
                when (authState) {
                    is AuthState.Success -> {
                        navController.navigate("main_screen") {
                            popUpTo("start_screen") { inclusive = true }
                        }
                    }
                    is AuthState.NotAuthenticated -> {
                        navController.navigate("signup_screen") {
                            popUpTo("start_screen") { inclusive = true }
                        }
                    }
                    else -> {
                        // 로딩 중 또는 기본 상태 처리
                        navController.navigate("signup_screen") {
                            popUpTo("start_screen") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
