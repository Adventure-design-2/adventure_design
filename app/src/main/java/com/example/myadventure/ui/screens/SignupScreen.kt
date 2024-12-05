@file:Suppress("DEPRECATION")

package com.example.myadventure.ui.screens


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myadventure.R
import com.example.myadventure.data.AuthManager
import com.example.myadventure.viewmodel.AuthState
import com.example.myadventure.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun SignUpScreen(navController: NavController, viewModel: AuthViewModel) {
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
            painter = painterResource(id = R.drawable.ic_app_logo2_1),
            contentDescription = "Logo",
            modifier = Modifier.size(170.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            SignUpContent(navController, viewModel)
        } else {
            LoginContent(navController, viewModel)
        }
    }
}

@Composable
fun SignUpContent(navController: NavController, viewModel: AuthViewModel) {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val authManager = AuthManager()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            if (account != null) {
                viewModel.signInWithGoogle(account.idToken ?: "")
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Google Login Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


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
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("or", modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        SocialMediaIcons(
            onGoogleSignIn = {
                val signInClient = authManager.getGoogleSignInClient(context, "YOUR_WEB_CLIENT_ID")
                val signInIntent = signInClient.signInIntent
                launcher.launch(signInIntent)
            }

        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank() && nickname.isNotBlank()) {
                    viewModel.registerWithEmail(email, password)
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

    when (authState) {
        is AuthState.Loading -> CircularProgressIndicator()
        is AuthState.Success -> {
            Toast.makeText(context, "회원가입 성공!", Toast.LENGTH_SHORT).show()
            navController.navigate("mission_screen") {
                popUpTo("signup_screen") { inclusive = true }
            }
        }
        is AuthState.Error -> {
            Toast.makeText(context, "회원가입 실패: ${(authState as AuthState.Error).message}", Toast.LENGTH_SHORT).show()
        }
        else -> {}
    }
}


@Composable
fun LoginContent(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
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
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text("or", modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        SocialMediaIcons(
            onGoogleSignIn = {
                viewModel.loginWithGoogle("YOUR_WEB_CLIENT_ID")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.loginWithEmail(email, password)
                } else {
                    Toast.makeText(context, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFFFFC0CB)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }
    }

    when (authState) {
        is AuthState.Loading -> CircularProgressIndicator()
        is AuthState.Success -> {
            Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
            navController.navigate("CouplecodeScreen") {
                popUpTo("auth_screen") { inclusive = true }
            }
        }
        is AuthState.Error -> {
            Toast.makeText(context, "로그인 실패: ${(authState as AuthState.Error).message}", Toast.LENGTH_SHORT).show()
        }
        else -> {}
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
fun SocialMediaIcons(onGoogleSignIn: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Google 로그인 추가
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google",
            modifier = Modifier
                .size(36.dp)
                .clickable { onGoogleSignIn() },
            tint = Color.Unspecified
        )
    }
}


