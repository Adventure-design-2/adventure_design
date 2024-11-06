package com.example.myadventure.ui.functions

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myadventure.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("캡슐") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ShopItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        ShopItem("정소연", R.drawable.ic_diary, 10),
        ShopItem("Yoga Mat", R.drawable.ic_diary, 15),
        ShopItem("Energy Pill", R.drawable.ic_diary, 5),
        ShopItem("Timer", R.drawable.ic_diary, 20),
        ShopItem("Capsule", R.drawable.ic_diary, 8)
    )

    Scaffold(
        containerColor = Color(0xFFF2E4DA),

        topBar = {
            TopAppBar(
                title = { Text("상점") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFFF2E4DA)),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(id = R.drawable.ic_arrow_back), contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(id = R.drawable.ic_point), contentDescription = "포인트")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("36")
                    }
                }
            )
        },
        bottomBar = { // 하단바 추가
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }, // 스낵바 추가
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Tab Row
                TabRow(selectedTabIndex = when (selectedTab) {
                    "캡슐" -> 0
                    "스티커" -> 1
                    "기타" -> 2
                    else -> 0
                }) {
                    Tab(
                        selected = selectedTab == "캡슐",
                        onClick = { selectedTab = "캡슐" }
                    ) { Text("캡슐") }

                    Tab(
                        selected = selectedTab == "스티커",
                        onClick = { selectedTab = "스티커" }
                    ) { Text("스티커") }

                    Tab(
                        selected = selectedTab == "기타",
                        onClick = { selectedTab = "기타" }
                    ) { Text("기타") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item List with 2-column grid layout
                LazyColumn {
                    itemsIndexed(items.chunked(2)) { index, rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                ShopItemCard(item, modifier = Modifier.weight(1f)) {
                                    selectedItem = item
                                    showDialog = true
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f)) // 빈 공간을 채워 2개 배치 맞추기
                            }
                        }
                        if (index < items.size / 2) Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )

    // Dialog for confirming purchase
    if (showDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("구매 하시겠습니까?") },
            text = { Text("상품: ${selectedItem?.name}") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("${selectedItem?.name}을(를) 샀습니다!")
                    }
                }) {
                    Text("예")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("아니오")
                }
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "닫기"
                )
            }
        )
    }
}

@Composable
fun ShopItemCard(item: ShopItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .aspectRatio(1f) // 정사각형 카드
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${item.price} 포인트", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Shop Item Data Class
data class ShopItem(val name: String, val imageRes: Int, val price: Int)
