package com.example.myadventure.ui.functions

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.io.File
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.myadventure.R

@Composable
fun DiaryScreens(navController: NavController) {
    val context = LocalContext.current
    val diaryEntries = remember { mutableStateListOf<DiaryEntry>() }

    // 파일 목록을 불러와서 diaryEntries 리스트에 추가
    LaunchedEffect(Unit) {
        diaryEntries.clear()
        diaryEntries.addAll(loadDiaryEntries(context))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Diary",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 2열로 표시하는 LazyVerticalGrid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2열로 구성
            verticalArrangement = Arrangement.spacedBy(16.dp), // 세로 간격
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 가로 간격
            modifier = Modifier.fillMaxSize()
        ) {
            items(diaryEntries) { entry ->
                DiaryEntryCard(entry)
            }
        }
    }
}

// 다이어리 항목 데이터 클래스
data class DiaryEntry(val imageUri: Uri, val date: String, val missionName: String, val comment: String)

// 파일에서 다이어리 항목을 불러오는 함수
fun loadDiaryEntries(context: Context): List<DiaryEntry> {
    // 예제 데이터: 실제 환경에서는 파일에서 불러오도록 구현
    val dummyEntries = mutableListOf<DiaryEntry>()
    repeat(20) { index ->
        dummyEntries.add(
            DiaryEntry(
                imageUri = Uri.parse("android.resource://com.example.myadventure/drawable/ic_launcher_foreground"),
                date = "2024-12-0${index + 1}",
                missionName = "벚꽃 데이트 ${index + 1}",
                comment = "This is comment for mission ${index + 1}."
            )
        )
    }
    return dummyEntries
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryCard(entry: DiaryEntry) {
    var showDialog by remember { mutableStateOf(false) }
//카드들 ui 수정하려면 여기서

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { showDialog = true }
            .fillMaxWidth() // 카드가 팝업창의 전체 너비를 차지하도록 설정
            .background(Color.White), // 배경색 설정
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 카드 배경색 (하얀색)

            // 카드 비율을 5:7로 설정

        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            val context = LocalContext.current
            val bitmap = BitmapFactory.decodeFile(entry.imageUri.path)

            if (bitmap != null) {
                // 파일 경로에서 Bitmap 이미지를 로드
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Diary Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5f / 7f) // 이미지 비율 동일하게 설정
                )
            } else {
                // painterResource로 기본 이미지 로드, 오류 시 대체 리소스 표시
                val painter = runCatching {
                    painterResource(id = R.drawable.dirarytest)
                }.getOrElse {
                    painterResource(id = android.R.drawable.ic_menu_gallery) // 대체 이미지
                }
                Image(
                    painter = painter,
                    contentDescription = "Default Diary Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5f / 7f) // 기본 이미지 비율도 동일하게 설정
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = entry.date, style = MaterialTheme.typography.bodyMedium)
            Text(text = entry.missionName, style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showDialog) { //팝업창 내용 수정
        Dialog(
            onDismissRequest = { showDialog = false })
        {
            Card(
                modifier = Modifier.padding(50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF),
                    contentColor = Color(0xFF000000)
                )
            ) {
                Text("20240412", style = MaterialTheme.typography.bodyMedium)
            }// text 색상을 정하는 코드(위에 세줄) 데이팅날짜
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val context = LocalContext.current
                val dialogBitmap = try {
                    // 이미지 파일이 존재하는 경우 디코딩
                    entry.imageUri.path?.let { path ->
                        BitmapFactory.decodeFile(path)
                    } ?: BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.dirarytest // 기본 이미지
                    )
                } catch (e: Exception) {
                    // 예외 발생 시 기본 이미지
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.dirarytest
                    )
                }

                //팝업창
                Box(
                    modifier = Modifier
                        .size(width = 500.dp, height = 450.dp)
                        .background(Color.White)
                        .padding(16.dp)
                ){
                    //이미지 생성 날짜
                    Text(text = entry.date, style = MaterialTheme.typography.bodyMedium)

                    Column (
                        modifier = Modifier.fillMaxSize(), // Column이 Box 크기를 가득 채움
                        horizontalAlignment = Alignment.CenterHorizontally, // 수평 정렬
                        verticalArrangement = Arrangement.Center // 수직 정렬
                    ){
                        Spacer(modifier = Modifier.height(20.dp))

                        // 이미지를 Bitmap 형태로 표시
                        Image(
                            bitmap = dialogBitmap.asImageBitmap(),
                            contentDescription = "Diary Image (Dialog)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Text(text = entry.missionName, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = entry.comment, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(onClick = { showDialog = false }) {
                            Text("닫기")
                        }

                    }

                }
            }
        }
    }
}



// 미리보기에서 더미 데이터 사용
@Preview(showBackground = true)
@Composable
fun PreviewDiaryScreen() {
    val dummyEntries = List(20) { index ->
        DiaryEntry(
            imageUri = Uri.parse("android.resource://com.example.myadventure/drawable/ic_launcher_foreground"),
            date = "2024-12-${index + 1}",
            missionName = "Mission ${index + 1}",
            comment = "Comment ${index + 1}"
        )
    }
    DiaryScreenPreviewWithData(dummyEntries)
}

@Composable
fun DiaryScreenPreviewWithData(diaryEntries: List<DiaryEntry>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(diaryEntries) { entry ->
            DiaryEntryCard(entry)
        }
    }
}