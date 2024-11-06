package com.example.myadventure.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun ProfileComposable(navController: NavController) {
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val scope = rememberCoroutineScope()

    var userName by rememberSaveable { mutableStateOf("사용자 이름") }
    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // DataStore에서 값 불러오기
    LaunchedEffect(Unit) {
        userPreferences.userNameFlow.collect { name ->
            userName = name
        }
        userPreferences.profileImageUriFlow.collect { uriString ->
            profileImageUri = uriString?.let { Uri.parse(it) }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            profileImageUri = saveImageToStorage(context, bitmap)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        profileImageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "프로필 사진",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .clickable {
                        galleryLauncher.launch("image/*")
                    }
            )
        } ?: Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .clickable {
                    galleryLauncher.launch("image/*")
                }
        ) {
            Text("프로필 사진을 선택하세요", modifier = Modifier.align(Alignment.Center))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("이름 변경") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            cameraLauncher.launch(null)
        }) {
            Text("카메라로 프로필 사진 찍기")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // 변경된 프로필 정보 저장
            scope.launch {
                userPreferences.saveUserName(userName)
                profileImageUri?.let { uri ->
                    userPreferences.saveProfileImageUri(uri.toString())
                }
            }
            // 홈 화면으로 이동
            navController.popBackStack()
        }) {
            Text("저장하고 돌아가기")
        }
    }
}

// 이미지 저장 함수
fun saveImageToStorage(context: Context, bitmap: Bitmap): Uri? {
    val filename = "profile_image_${System.currentTimeMillis()}.jpg"
    var fos: FileOutputStream? = null
    return try {
        val file = File(context.filesDir, filename)
        fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        Uri.fromFile(file)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        fos?.close()
    }
}
