package com.example.myadventure.ui.functions

import android.Manifest
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.compose.ui.platform.LocalFocusManager // 추가된 import 문
import java.text.SimpleDateFormat
import java.util.Locale // 추가된 import 문
import java.util.Date // 추가된 import 문

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(navController: NavController) {
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var isCommentEnabled by remember { mutableStateOf(false) }
    var isCommentRegistered by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current // 키보드 내리기 위한 FocusManager

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val missionName = "미션 이름 예시" // 미션명 추가
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // 현재 날짜 추가

    fun saveBitmapToFile(context: Context, bitmap: Bitmap): Uri? {
        val file = File(context.filesDir, "diary_entry_${System.currentTimeMillis()}.jpg")
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

    // 이미지와 코멘트, 미션명, 날짜를 하나의 비트맵으로 결합하는 함수
    fun combineImageAndComment(image: Bitmap, missionName: String, date: String, comment: String): Bitmap {
        val combinedBitmap = Bitmap.createBitmap(image.width, image.height + 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawBitmap(image, 0f, 100f, null)

        val paint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        // 날짜와 미션명을 이미지 상단에 추가
        canvas.drawText("미션: $missionName", 20f, 50f, paint)
        canvas.drawText("날짜: $date", 20f, 100f, paint)

        // 코멘트를 이미지 하단에 추가
        canvas.drawText(comment, 20f, (image.height + 160).toFloat(), paint)
        return combinedBitmap
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val uri = saveBitmapToFile(context, bitmap)
            if (uri != null) {
                selectedImageUri = uri
                Toast.makeText(context, "사진이 성공적으로 찍혔습니다!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "갤러리에서 사진을 선택했습니다!", Toast.LENGTH_SHORT).show()
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
                                .clickable {
                                    cameraLauncher.launch(null)
                                }
                        )
                        Text("카메라", fontSize = 16.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_galary),
                            contentDescription = "갤러리",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    galleryLauncher.launch("image/*")
                                }
                        )
                        Text("갤러리", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("댓글이 등록 완료되었습니다")
                        }
                        isCommentRegistered = true
                        focusManager.clearFocus() // 키보드를 내리도록 추가
                    },
                    enabled = isCommentEnabled && commentText.text.isNotBlank() && !isCommentRegistered,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCommentRegistered) Color.Gray else Color(0xFFFFC0CB)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isCommentRegistered) "등록 완료" else "댓글 등록")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedImageUri?.let { uri ->
                            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            val combinedBitmap = combineImageAndComment(bitmap, missionName, date, commentText.text)
                            val fileUri = saveBitmapToFile(context, combinedBitmap)

                            if (fileUri != null) {
                                val screenshotFile = File(context.filesDir, "mission_screenshot_${System.currentTimeMillis()}.jpg")
                                val outputStream = FileOutputStream(screenshotFile)
                                combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream.flush()
                                outputStream.close()
                                Toast.makeText(context, "스크린샷이 저장되었습니다!", Toast.LENGTH_SHORT).show()
                                navController.navigate("garden_screen")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC0CB)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("미션 완료")
                }
            }



            // 코멘트 등록 팝업
            if (showCommentRegisteredDialog) {
                AlertDialog(
                    onDismissRequest = { showCommentRegisteredDialog = false },
                    title = { Text("코멘트 등록") },
                    text = { Text("코멘트가 등록되었습니다!") },
                    confirmButton = {
                        TextButton(onClick = { showCommentRegisteredDialog = false }) {
                            Text("확인")
                        }
                    }
                )
            }

            // 미션 성공 팝업
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("미션 성공") },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 이미지 추가
                        Image(
                            painter = painterResource(id = R.drawable.ic_reward2), // 이미지 리소스 추가
                            contentDescription = "Success Image",
                            modifier = Modifier.size(100.dp) // 이미지 크기 조절
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("하트코인을 얻었어요!!", style = MaterialTheme.typography.headlineSmall, color = Color(0xFFE91E63))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("X 100", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
                    }
                    },
                    confirmButton = {
                        var showSuccessDialog = remember { mutableStateOf(false) } // 성공 팝업 표시 여부

                    }
                )
            }


        }
    )
}



