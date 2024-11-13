package com.example.myadventure.backend.server

import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

val clients = mutableListOf<Socket>()

// 클라이언트 연결을 처리하는 함수
fun handleClient(clientSocket: Socket) {
    val clientAddress = clientSocket.inetAddress.hostAddress
    println("새로운 클라이언트 연결: $clientAddress")

    try {
        val inputStream = clientSocket.getInputStream()
        val outputStream = clientSocket.getOutputStream()

        while (true) {
            val buffer = ByteArray(1024)
            val bytesRead = inputStream.read(buffer)
            if (bytesRead == -1) break  // 연결이 끊어졌으면 종료

            val message = String(buffer, 0, bytesRead, Charsets.UTF_8)
            println("받은 메시지: $message")

            // 연결된 모든 클라이언트에게 메시지 전송
            for (client in clients) {
                if (client != clientSocket) {
                    client.getOutputStream().write(message.toByteArray(Charsets.UTF_8))
                }
            }
        }
    } catch (e: Exception) {
        println("오류 발생: ${e.message}")
    } finally {
        clients.remove(clientSocket)
        clientSocket.close()
    }
}

// 서버 시작 함수
fun startServer() {
    val serverSocket = ServerSocket(5555)
    println("서버가 시작되었습니다. 클라이언트를 기다립니다...")

    while (true) {
        val clientSocket = serverSocket.accept()
        clients.add(clientSocket)

        // 각 클라이언트에 대해 별도의 스레드로 처리
        thread { handleClient(clientSocket) }
    }
}

fun main() {
    startServer()
}
