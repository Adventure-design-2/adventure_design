package com.example.myadventure.ui.screens.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FeatureCard(navController: NavController, route: String, text: String, backgroundRes: Int) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp)
            .clickable {
                navController.navigate(route)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

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
