package com.example.myadventure.ui.functions

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.myadventure.model.Record
import com.example.myadventure.viewmodel.RecordViewModel
import kotlinx.coroutines.launch

@Composable
fun DiaryScreens(navController: NavController, recordViewModel: RecordViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val records by recordViewModel.records.collectAsState(initial = emptyList())

    // Load records from the local database or source
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            recordViewModel.loadRecordsFromLocal(context)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Diary",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(records) { record ->
                DiaryEntryCard(record)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEntryCard(record: Record) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable { showDialog = true }
            .fillMaxWidth()
            .background(Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            val context = LocalContext.current
            val bitmap = try {
                BitmapFactory.decodeFile(Uri.parse(record.image).path)
            } catch (e: Exception) {
                null
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Diary Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5f / 7f)
                )
            } else {
                // Placeholder image
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Default Diary Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5f / 7f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = record.timestamp.toString(), style = MaterialTheme.typography.bodyMedium)
            Text(text = record.title, style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            Card(
                modifier = Modifier.padding(50.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val context = LocalContext.current
                    val dialogBitmap = try {
                        BitmapFactory.decodeFile(Uri.parse(record.image).path)
                    } catch (e: Exception) {
                        null
                    }

                    dialogBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Diary Image (Dialog)",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                    }
                    Text(text = record.title, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = record.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = { showDialog = false }) {
                        Text("닫기")
                    }
                }
            }
        }
    }
}
