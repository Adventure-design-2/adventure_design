package com.example.myadventure.ui.functions


import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext


@Composable
fun DiaryScreen(navController: NavController) {
    val context = LocalContext.current
    val diaryEntries = remember { mutableStateListOf<DiaryEntry>() }

    // 파일 목록을 불러와서 diaryEntries 리스트에 추가
    LaunchedEffect(Unit) {
        diaryEntries.clear()
        diaryEntries.addAll(loadDiaryEntries(context))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(diaryEntries) { entry ->
            DiaryEntryCard(entry)
        }
    }
}

// 다이어리 항목 데이터 클래스
data class DiaryEntry(val imageUri: Uri, val date: String, val missionName: String, val comment: String)

// 파일에서 다이어리 항목을 불러오는 함수
fun loadDiaryEntries(context: Context): List<DiaryEntry> {
    val entries = mutableListOf<DiaryEntry>()
    val filesDir = context.filesDir
    val files = filesDir.listFiles { file -> file.name.startsWith("diary_entry_") && file.extension == "jpg" }

    files?.forEach { file ->
        val uri = Uri.fromFile(file)
        val metadata = file.nameWithoutExtension.split("_")
        val date = metadata.getOrNull(2) ?: "Unknown Date"
        val missionName = metadata.getOrNull(3) ?: "Unknown Mission"
        val comment = metadata.getOrNull(4) ?: "No Comment"
        entries.add(DiaryEntry(uri, date, missionName, comment))
    }
    return entries
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryCard(entry: DiaryEntry) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 이미지 미리보기
            Image(
                bitmap = BitmapFactory.decodeFile(entry.imageUri.path).asImageBitmap(),
                contentDescription = "Diary Image",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = entry.date, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = entry.missionName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    // 다이어리 항목 팝업
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = BitmapFactory.decodeFile(entry.imageUri.path).asImageBitmap(),
                        contentDescription = "Diary Image",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = entry.date, style = MaterialTheme.typography.bodyMedium)
                    Text(text = entry.missionName, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = entry.comment, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showDialog = false }) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}