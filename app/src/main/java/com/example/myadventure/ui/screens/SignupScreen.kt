package com.example.myadventure.ui.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myadventure.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Sign up") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F8))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.app_logo4_1),
            contentDescription = "Logo",
            modifier = Modifier.size(170.dp)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // Tab Row for switching between Login and Sign up
        TabRow(
            selectedTabIndex = if (selectedTab == "Sign up") 1 else 0,
            containerColor = Color(0xFFFFF5F8)
        ) {
            Tab(
                selected = selectedTab == "Log in",
                onClick = { selectedTab = "Log in" }
            ) { Text("Log in") }


            Tab(
                selected = selectedTab == "Sign up",
                onClick = { selectedTab = "Sign up" }
            ) { Text("Sign up") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTab == "Sign up") {
            SignUpContent(navController)
        } else {
            LoginContent(navController)
        }
    }
}

@Composable
fun SignUpContent(navController: NavController) {0
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextFieldWithCloseIcon("Nickname", nickname) { nickname = it }
        TextFieldWithCloseIcon("Email", email) { email = it }
        TextFieldWithCloseIcon("Password", password) { password = it }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier = Modifier.weight(1f))
            Text("or", modifier = Modifier.padding(horizontal = 8.dp))
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        SocialMediaIcons()

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank() && nickname.isNotBlank()) {
                    // 로그인처럼 home_screen으로 바로 이동
                    navController.navigate("mission_screen") {
                        popUpTo("signup_screen") { inclusive = true }
                    }
                    Toast.makeText(context, "회원가입 성공! 로그인되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFFFFC0CB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign up")
        }
    }
}

@Composable
fun LoginContent(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("이전에 애플 로그인을 하셨습니다.", fontSize = 14.sp, color = Color.Gray)

        TextFieldWithCloseIcon("Email", email) { email = it }
        TextFieldWithCloseIcon("Password", password) { password = it }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Forgot Password?", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier = Modifier.weight(1f))
            Text("or", modifier = Modifier.padding(horizontal = 8.dp))
            Divider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        SocialMediaIcons()

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // 로그인 성공 메시지 Toast로 표시
                Toast.makeText(context, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()

                // 다음 화면으로 이동
                navController.navigate("CouplecodeScreen") {
                    popUpTo("auth_screen") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFFFFC0CB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }
    }
}


@Composable
fun TextFieldWithCloseIcon(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        singleLine = true
    )
}


@Composable
fun SocialMediaIcons() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_apple),
            contentDescription = "Apple",
            modifier = Modifier.size(36.dp), // 아이콘 크기 설정
            tint = Color.Unspecified
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier.size(36.dp), // 아이콘 크기 설정
            tint = Color.Unspecified
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_facebook),
            contentDescription = "Facebook",
            modifier = Modifier.size(36.dp), // 아이콘 크기 설정
            tint = Color.Unspecified
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_kakao),
            contentDescription = "Kakao",
            modifier = Modifier.size(39.dp), // 아이콘 크기 설정
            tint = Color.Unspecified
        )
    }
}

