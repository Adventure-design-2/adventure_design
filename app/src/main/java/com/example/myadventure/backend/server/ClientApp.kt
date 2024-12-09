package com.example.myadventure.backend.server

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.Socket

class ClientAppCompose : ComponentActivity() {
    private val serverIp = "서버_IP_주소"
    private val serverPort = 5555
    private lateinit var clientSocket: Socket
    private lateinit var outputStream: OutputStream

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ClientScreen(
                    onSendMessage = { message ->
                        sendMessage(message)
                    },
                    onConnect = {
                        startClient()
                    }
                )
            }
        }
    }

    // 서버로 메시지를 보내는 함수
    private fun sendMessage(message: String) {
        if (::outputStream.isInitialized && message.isNotEmpty()) {
            outputStream.write(message.toByteArray(Charsets.UTF_8))
        }
    }

    // 서버와 연결하는 함수
    private fun startClient() {
        clientSocket = Socket(serverIp, serverPort)
        outputStream = clientSocket.getOutputStream()
    }
}

@Composable
fun ClientScreen(onSendMessage: (String) -> Unit, onConnect: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var chatLog by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // 서버 연결 시도
        onConnect()
        scope.launch(Dispatchers.IO) {
            // 메시지 수신을 위한 스레드
            val inputStream = Socket("서버_IP_주소", 5555).getInputStream()
            val buffer = ByteArray(1024)
            while (true) {
                try {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) break
                    val receivedMessage = String(buffer, 0, bytesRead, Charsets.UTF_8)
                    chatLog += "상대방: $receivedMessage\n"
                } catch (e: Exception) {
                    println("오류 발생: ${e.message}")
                    break
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("메시지 입력") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            scope.launch {
                onSendMessage(message)
                message = "" // 메시지 전송 후 텍스트 필드 초기화
            }
        }) {
            Text("메시지 보내기")
        }
        Text(text = chatLog, modifier = Modifier.fillMaxWidth())
    }
}

