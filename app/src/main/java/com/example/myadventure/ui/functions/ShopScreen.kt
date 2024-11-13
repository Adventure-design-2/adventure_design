package com.example.myadventure.ui.functions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

    val capsuleItems = listOf(
        ShopItem("해캡이", R.drawable.ic_sun, 500),
        ShopItem("반토리", R.drawable.ic_groundhog, 300),
        ShopItem("분또기", R.drawable.ic_snail, 200),
        ShopItem("달도치", R.drawable.ic_hedgehog, 100),
        ShopItem("캡슈리", R.drawable.ic_pill, 30)
    )
    val stickerItems = listOf(
        ShopItem("하트", R.drawable.ic_heart, 10),
        ShopItem("곰돌이 인형", R.drawable.ic_teddybear, 10),
        ShopItem("사과", R.drawable.ic_apple2, 10),
        ShopItem("?", R.drawable.ic_question, 10),
        ShopItem("곰돌이 인형", R.drawable.ic_teddybear, 10),
        ShopItem("사과", R.drawable.ic_apple2, 10)
    )

    val items = when (selectedTab) {
        "캡슐" -> capsuleItems
        "스티커" -> stickerItems
        else -> capsuleItems
    }

    Scaffold(
        containerColor = Color(0xFFF2E4DA),
        topBar = {
            TopAppBar(
                title = { Text("SHOP") },
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
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // Tab Row
                TabRow(
                    selectedTabIndex = when (selectedTab) {
                        "캡슐" -> 0
                        "스티커" -> 1
                        "기타" -> 2
                        else -> 0
                    },
                    containerColor = Color(0xFFF2E4DA),
                ) {
                    Tab(
                        selected = selectedTab == "캡슐",
                        onClick = { selectedTab = "캡슐" }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (selectedTab == "캡슐") Color(0xFFFFC0CB) else Color.Transparent)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                "캡슐",
                                color = if (selectedTab == "캡슐") Color.Black else Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Tab(
                        selected = selectedTab == "스티커",
                        onClick = { selectedTab = "스티커" }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (selectedTab == "스티커") Color(0xFFFFC0CB) else Color.Transparent)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                "스티커",
                                color = if (selectedTab == "스티커") Color.Black else Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
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
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        if (index < items.size / 2) Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    )

    // 구매 확인 다이얼로그
    if (showDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("구매 하시겠습니까?") },
            text = { Text("상품: ${selectedItem?.name}") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("${selectedItem?.name}을(를) 구매했습니다!")
                    }
                    // GardenScreen으로 돌아가면서 showCapsuleDialogInitially를 true로 전달
                    navController.navigate("garden_screen") {
                        popUpTo("garden_screen") { inclusive = true }
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
            .aspectRatio(1f) // 정사각형 아이템 box
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(80.dp)
                    .background(Color.Transparent)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = item.name, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${item.price} 포인트", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// Shop Item Data Class
data class ShopItem(val name: String, val imageRes: Int, val price: Int)
