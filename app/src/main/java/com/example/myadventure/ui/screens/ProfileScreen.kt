package com.example.myadventure.ui.screens

import DDayDataStore
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myadventure.model.UserProfile
import com.example.myadventure.viewmodel.AuthViewModel
import com.google.firebase.storage.FirebaseStorage

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    navController: NavController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val dDayDataStore = DDayDataStore()
    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var dDay by remember { mutableStateOf("D-Day 설정 안 됨") }
    var isLoading by remember { mutableStateOf(false) }

    val storage = FirebaseStorage.getInstance()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Firestore에서 데이터 로드
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile { profile ->
            profile?.let {
                name = it.name
                imageUrl = it.imageUrl
            }
        }
        // D-Day 로드
        dDayDataStore.getDDayFlow(context).collect { savedDDay ->
            dDay = savedDDay?.let { calculateDDay(it) } ?: "D-Day 설정 안 됨"
        }
    }

    // 이미지 선택 및 업로드
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            val fileName = "${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("profile_images/$fileName")

            storageRef.putFile(it)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        imageUrl = downloadUri.toString()
                        Toast.makeText(context, "이미지가 업로드되었습니다!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "이미지 업로드 실패!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Scaffold(
        containerColor = Color(0xFFFFF5F8), // 분홍 배경색
        bottomBar = {
            BottomNavigationBar(navController = navController) // BottomNavigationBar 호출
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 상단에 "프로필" 텍스트
            Text(
                text = "프로필",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 프로필 이미지
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(220.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.LightGray),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8BBD0)),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("이미지 선택")
            }

            // 이름 입력 필드
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // D-Day 표시 및 이동 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "D-Day: $dDay", fontSize = 16.sp)
                Button(
                    onClick = { navController.navigate("dday_screen") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8BBD0)),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("변경", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 초대코드 이동 버튼
            Button(
                onClick = { navController.navigate("couplecode_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8BBD0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text("초대코드 관리", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 프로필 저장 버튼
            Button(
                onClick = {
                    isLoading = true
                    val profile = UserProfile(
                        uid = viewModel.getCurrentUserId(),
                        name = name,
                        imageUrl = imageUrl
                    )
                    viewModel.saveUserProfile(profile) { success ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "프로필이 저장되었습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "프로필 저장에 실패했습니다!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8BBD0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "저장 중..." else "프로필 저장")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 로그아웃 버튼
            Button(
                onClick = {
                    viewModel.logout() // 로그아웃 처리
                    onLogout() // 로그아웃 후 콜백 호출
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCDD2)),
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
                    .align(alignment = Alignment.End)
            ) {
                Text("로그아웃", fontSize = 16.sp)
            }
        }
    }
}






