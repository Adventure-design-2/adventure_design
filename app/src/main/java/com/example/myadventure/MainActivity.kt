package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.ui.screens.SignUpScreen
import com.example.myadventure.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController: NavHostController = rememberNavController()
            val authViewModel = AuthViewModel()

            NavHost(
                navController = navController,
                startDestination = "signup_screen"
            ) {
                composable("signup_screen") {
                    SignUpScreen(navController = navController, viewModel = authViewModel)
                }
            }
        }
    }
}
