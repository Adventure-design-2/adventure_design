package com.example.myadventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.data.MissionRepository
import com.example.myadventure.ui.screens.MissionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MissionRepository를 통해 미션 데이터 로드
        val repository = MissionRepository(this)

        setContent {
            val navController = rememberNavController()
            MissionScreen(
                navController = navController,
                repository = repository
            )
        }
    }
}
