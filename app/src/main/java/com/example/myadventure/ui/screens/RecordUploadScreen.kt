package com.example.myadventure.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.R
import com.example.myadventure.model.Record
import com.example.myadventure.viewmodel.RecordViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordUploadScreen(navController: NavController, viewModel: RecordViewModel) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var isCommentEnabled by remember { mutableStateOf(false) }
    var isCommentRegistered by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 카메라 및 갤러리 런처 설정
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToFile(context, bitmap)
            if (uri != null) {
                selectedImageUri = uri
                isCommentEnabled = true
            } else {
                Toast.makeText(context, "사진 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            isCommentEnabled = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF2E4DA),
        topBar = {
            TopAppBar(
                title = { Text("인증사진 기록") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF2E4DA))
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 이미지 선택 및 미리보기
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    selectedImageUri?.let { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "선택된 이미지",
                            modifier = Modifier.size(200.dp)
                        )
                    } ?: run {
                        Text("미션인증사진", color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 카메라 및 갤러리 버튼
                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "카메라",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { cameraLauncher.launch(null) }
                        )
                        Text("카메라", fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_diary),
                            contentDescription = "갤러리",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { galleryLauncher.launch("image/*") }
                        )
                        Text("갤러리", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 댓글 입력
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("댓글을 입력하세요...") },
                    enabled = isCommentEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 댓글 등록 버튼
                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("댓글이 등록 완료되었습니다")
                        }
                        isCommentRegistered = true
                    },
                    enabled = isCommentEnabled && commentText.text.isNotBlank() && !isCommentRegistered,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isCommentRegistered) "등록 완료" else "댓글 등록")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 저장 및 업로드 버튼
                Button(
                    onClick = {
                        selectedImageUri?.let { uri ->
                            val record = Record(
                                recordId = "",
                                title = "미션 제목",
                                description = commentText.text,
                                image = uri.toString(),
                                authorUid = "currentUserUid",
                                partnerUid = "partnerUid"
                            )
                            viewModel.saveRecord(record)
                            Toast.makeText(context, "기록이 저장되었습니다!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("미션 완료")
                }
            }
        }
    )
}

// 유틸리티 함수: 비트맵을 로컬에 저장
fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
    val file = File(context.filesDir, "record_${System.currentTimeMillis()}.jpg")
    return try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        Uri.fromFile(file)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


@Preview
@Composable
fun RecordUploadScreenPreview() {
    val navController = rememberNavController()
    val viewModel = RecordViewModel(LocalContext.current)
    RecordUploadScreen(navController, viewModel)
}