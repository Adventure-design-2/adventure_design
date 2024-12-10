package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.Coil
import coil.ImageLoader
import coil.request.CachePolicy
import com.example.myadventure.data.MissionRepository
import com.example.myadventure.ui.screens.ChatRoomListScreen
import com.example.myadventure.ui.screens.ChatRoomScreen
import com.example.myadventure.ui.screens.CouplecodeScreen
import com.example.myadventure.ui.screens.DDayScreen
import com.example.myadventure.ui.screens.MainScreen
import com.example.myadventure.ui.screens.MissionScreen
import com.example.myadventure.ui.screens.ProfileScreen
import com.example.myadventure.ui.screens.RecordUploadScreen
import com.example.myadventure.ui.screens.SignUpScreen
import com.example.myadventure.viewmodel.AuthState
import com.example.myadventure.viewmodel.AuthViewModel
import com.example.myadventure.viewmodel.InviteViewModel
import com.example.myadventure.viewmodel.RecordViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
        Coil.setImageLoader(imageLoader)

        setContent {
            val navController: NavHostController = rememberNavController()
            val authViewModel: AuthViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
            val recordViewModel: RecordViewModel = ViewModelProvider(this)[RecordViewModel::class.java]
            val missionRepository = MissionRepository(this)

            // `authState` 상태 관찰
            val authState by authViewModel.authState.collectAsState()

            // 로그인 상태 확인
            val isLoggedIn = authState is AuthState.Authenticated

            // AppNavHost 호출
            AppNavHost(
                navController = navController,
                isLoggedIn = isLoggedIn,
                authViewModel = authViewModel,
                recordViewModel = recordViewModel,
                missionRepository = missionRepository
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    authViewModel: AuthViewModel,
    recordViewModel: RecordViewModel,
    missionRepository: MissionRepository
) {
    // 초기 화면 설정: 로그인 여부에 따라 결정
    val startDestination = if (isLoggedIn) "main_screen" else "signup_screen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("signup_screen") {
            SignUpScreen(navController = navController, viewModel = authViewModel)
        }
        composable("main_screen") {
            MainScreen(navController = navController)
        }
        composable("mission_screen") {
            MissionScreen(navController = navController, repository = missionRepository)
        }
        composable("diary_screen") {
            ChatRoomListScreen(
                navController = navController,
                viewModel = authViewModel,
                onRoomSelected = { roomId ->
                    navController.navigate("chat_room_screen/$roomId")
                }
            )
        }
        composable("recordupload_screen/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            RecordUploadScreen(
                roomId = roomId,
                viewModel = recordViewModel,
                navController = navController
            )
        }
        composable("other_screen") {
            ProfileScreen(viewModel = authViewModel, navController = navController,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("signup_screen") {
                        popUpTo("main_screen") { inclusive = true }
                    }
                })
        }
        composable("couplecode_Screen") {
            CouplecodeScreen(
                navController = navController,
                viewModel = InviteViewModel(),
                authViewModel = authViewModel,
                userUid = authViewModel.getCurrentUserId()
            )
        }
        composable("dday_screen") {
            DDayScreen(navController = navController)
        }
        composable("chat_room_screen/{roomId}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            ChatRoomScreen(
                navController = navController,
                roomId = roomId
            )
        }
    }
}
