package com.example.myadventure.ui.functions

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myadventure.R
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    navController: NavController,
    missionTitle: String
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        topBar = {
            TopAppBar(
                title = { Text("미션") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFF2E4DA)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "뒤로가기",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 닫기 기능 추가 */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "닫기",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 미션 아이콘
                Icon(
                    painter = painterResource(id = R.drawable.ic_mail),
                    contentDescription = "미션 아이콘",
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 미션 제목
                Text(
                    text = "상남자/상여자 되기 미션!",
                    color = Color(0xFFD81B60),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 미션 설명
                Text(
                    text = buildAnnotatedString {
                        append("카페에서 음료를 마실 때, 그 누구도 상상하지 못했던 \n")
                        append("‘강력한 스타일’을 보여주고 싶은 사람을 위한 미션! \n")
                        append("당신이 바로 ")

                        pushStyle(SpanStyle(color = Color(0xFFD81B60), fontWeight = FontWeight.Bold))
                        append("상남자")
                        pop()

                        append(" 혹은 ")

                        pushStyle(SpanStyle(color = Color(0xFFD81B60), fontWeight = FontWeight.Bold))
                        append("상여자")
                        pop()

                        append("가 될 차례입니다.\n이 미션의 핵심은 아주 간단합니다! 빨대? 필요 없다! \n")
                        append("그냥 컵을 손에 들고, 진지하게 음료를 마시는 미션!\n")
                        append("누군가는 편리함을 위해 빨대를 사용하지만, ")

                        pushStyle(SpanStyle(color = Color(0xFFD81B60), fontWeight = FontWeight.Bold))
                        append("상남자/상여자")
                        pop()

                        append("는 달라야죠.\n별다른 장비 없이,\n")
                        append("음료를 완벽하게 즐기는 멋짐을 발산하세요.\n")
                        append("이 미션은 단순히 ‘다르게 마시기’ 그 이상의 의미!!\n")
                        append("빨대를 사용하지 않고 음료를 마시는 것은,\n")
                        append("자기 확신과 자유로움의 상징!\n")
                        append("여러분의 당당한 자세, 카페에서 주목받을 준비 되셨나요?\n")
                        append("이제 여러분도 ")

                        pushStyle(SpanStyle(color = Color(0xFFD81B60), fontWeight = FontWeight.Bold))
                        append("상남자/상여자")
                        pop()

                        append("가 되어보세요!!")
                    },
                    fontSize = 15.sp,
                    color = Color(0xFF6D4C41),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 카메라 아이콘
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "카메라 아이콘",
                        modifier = Modifier
                            .size(100.dp)
                            .clickable {
                                // 팝업 메시지 표시하고 다음 화면으로 이동
                                Toast.makeText(context, "인증하러 갑니다", Toast.LENGTH_SHORT).show()
                                navController.navigate("verification_screen")
                            }
                    )
                    Text(
                        text = "미션 성공 인증",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}
