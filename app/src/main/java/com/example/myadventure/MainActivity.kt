package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.ui.theme.MyAdventureTheme
import com.example.myadventure.ui.functions.*
import com.example.myadventure.ui.profile.ProfileComposable

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
//                    composable("diary_screen") {
//                        DiaryScreen()
//                    }
//                    composable("garden_screen") {
//                        GardenScreen()
//                    }
                    composable("map_screen") {
                        MapScreen()
                    }
                    composable("profile_screen") {
                        ProfileComposable(navController = navController)
                    }
                }
            }
        }
    }
}
