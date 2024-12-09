package com.example.myadventure.ui.compose

// 기본 Android 및 Compose 라이브러리

// 프로젝트 내의 각 컴포넌트 import
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myadventure.ui.screens.FeatureCard


data class Feature(
    val route: String,
    val text: String,
    val backgroundRes: Int
)


@Composable
fun FeatureCardRow(
    navController: NavController,
    firstFeature: Feature,
    secondFeature: Feature
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        FeatureCard(
            navController = navController,
            route = firstFeature.route,
            text = firstFeature.text,
            backgroundRes = firstFeature.backgroundRes
        )
        FeatureCard(
            navController = navController,
            route = secondFeature.route,
            text = secondFeature.text,
            backgroundRes = secondFeature.backgroundRes
        )
    }
}

@Composable
fun FeatureCard(
    navController: NavController,
    route: String,
    text: String,
    backgroundRes: Int
) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable { navController.navigate(route) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = backgroundRes),
                contentDescription = text,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Preview
@Composable
fun PreviewFeatureCard(){
    val navController = NavController(LocalContext.current)
    FeatureCard(navController = navController, route = "", text = "D-Day", backgroundRes = 0)

}