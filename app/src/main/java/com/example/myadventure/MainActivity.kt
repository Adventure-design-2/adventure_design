package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.data.MissionRepository
import com.example.myadventure.ui.screens.MissionScreen
import com.example.myadventure.ui.screens.RecordUploadScreen
import com.example.myadventure.ui.screens.SignUpScreen
import com.example.myadventure.viewmodel.AuthState
import com.example.myadventure.viewmodel.AuthViewModel
import com.example.myadventure.viewmodel.RecordViewModel
import com.example.myadventure.viewmodel.RecordViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController: NavHostController = rememberNavController()
            val authViewModel: AuthViewModel = viewModel()
            val recordViewModel: RecordViewModel = ViewModelProvider(
                this,
                RecordViewModelFactory(applicationContext) // Context 전달
            )[RecordViewModel::class.java]

            val repository = MissionRepository(this)

            // 로그인 상태 확인
            authViewModel.checkAuthState()

            // 화면 전환
            NavHost(navController, startDestination = "signup_screen") {
                composable("signup_screen") {
                    SignUpScreen(
                        navController = navController,
                        viewModel = authViewModel
                    )
                }
                composable("mission_screen") {
                    MissionScreen(
                        navController = navController,
                        repository = repository
                    )
                }
                composable("record_upload_screen") {
                    RecordUploadScreen(
                        navController = navController,
                        viewModel = recordViewModel
                    )
                }
            }

            // 로그인 상태에 따라 초기 화면 설정
            when (val authState = authViewModel.authState.collectAsState().value) {
                is AuthState.Success -> {
                    navController.navigate("mission_screen") {
                        popUpTo("signup_screen") { inclusive = true }
                    }
                }
                is AuthState.NotAuthenticated -> {
                    navController.navigate("signup_screen") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                else -> {
                    // 로딩 중이거나 초기 상태
                }
            }
        }
    }
}
