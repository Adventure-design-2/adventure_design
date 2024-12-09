package com.example.myadventure.ui.screens

import DDayDataStore
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myadventure.data.DDayRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DDayScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dDayRepository = DDayRepository()
    val dDayDataStore = DDayDataStore()

    // Firebase Auth로 현재 사용자 UID 가져오기
    val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // DataStore에서 D-Day Flow 수집
    val savedDDayFlow = dDayDataStore.getDDayFlow(context).collectAsState(initial = null)

    // D-Day 값 관리
    var dDayInput by remember { mutableStateOf("") }
    var dDayResult by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // 로컬 및 Firebase에서 D-Day 값 불러오기
    LaunchedEffect(userUid) {
        isLoading = true

        savedDDayFlow.value?.let { localDDay ->
            dDayInput = localDDay
            dDayResult = calculateDDay(localDDay)
        } ?: run {
            // Firebase에서 불러오기
            if (userUid.isNotEmpty()) {
                val savedDDay = dDayRepository.getDDay(userUid)
                if (!savedDDay.isNullOrEmpty()) {
                    dDayInput = savedDDay
                    dDayResult = calculateDDay(savedDDay)

                    // 로컬 DataStore에 저장
                    coroutineScope.launch {
                        dDayDataStore.saveDDay(context, savedDDay)
                    }
                }
            }
        }

        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF4F7)) // 연한 분홍색 배경
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFFFFC6D3)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "두 분의 기념일을 입력하세요",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = dDayInput,
                    onValueChange = { newValue ->
                        dDayInput = autoFormatDateInput(newValue)
                    },
                    placeholder = { Text(text = "YYYY-MM-DD") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isDateValid(dDayInput)) {
                            coroutineScope.launch {
                                isLoading = true
                                val success = dDayRepository.saveDDay(userUid, dDayInput)
                                if (success) {
                                    // 로컬 DataStore에 저장
                                    dDayDataStore.saveDDay(context, dDayInput)

                                    dDayResult = calculateDDay(dDayInput)
                                    navController.navigate("other_screen")
                                } else {
                                    Toast.makeText(context, "D-Day 저장 실패", Toast.LENGTH_SHORT).show()
                                }
                                isLoading = false
                            }
                        } else {
                            Toast.makeText(context, "올바른 날짜를 입력하세요.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC6D3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (dDayResult.isNotEmpty()) {
                    Text(
                        text = dDayResult,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}





// 날짜 유효성 검사 함수
fun isDateValid(date: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.isLenient = false
        sdf.parse(date) != null
    } catch (e: Exception) {
        false
    }
}

// "-" 자동 추가 기능
fun autoFormatDateInput(input: String): String {
    val digitsOnly = input.replace("-", "")
    val builder = StringBuilder()

    for (i in digitsOnly.indices) {
        if (i == 4 || i == 6) builder.append("-")
        builder.append(digitsOnly[i])
    }

    return builder.toString()
}



@SuppressLint("SimpleDateFormat")
fun calculateDDay(targetDate: String?): String {
    return try {
        if (targetDate.isNullOrEmpty()) {
            return "날짜를 찾을 수 없습니다."
        }

        // 날짜 형식 검증
        if (!targetDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return "날짜 형식이 잘못되었습니다."
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // Firebase 데이터를 UTC로 가정
        }
        val todayDate = Date() // 오늘 날짜
        val targetDateParsed = sdf.parse(targetDate)

        if (targetDateParsed != null) {
            val diff = (todayDate.time - targetDateParsed.time) / (1000 * 60 * 60 * 24)
            when {
                diff < 0 -> "D-${-diff}" // 미래의 날짜
                diff == 0L -> "오늘이 기념일입니다! 🎉"
                else -> "D+$diff" // 지난 날짜
            }
        } else {
            "날짜를 올바르게 입력해주세요."
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "날짜를 올바르게 입력해주세요."
    }
}

