package com.example.myadventure.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PointDisplay(points: Int) {
    Box(
        modifier = Modifier
            .padding(15.dp) // 15dp 정동의 패딩
            .widthIn(min = 100.dp, max = 150.dp) // 글자 크기와 개수를 감안한 최소, 최대 너비
            .height(50.dp) // 숫자와 글자 크기를 위한 높이
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$points 점",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 15.dp) // 좌우 패딩 적용
        )
    }
}
