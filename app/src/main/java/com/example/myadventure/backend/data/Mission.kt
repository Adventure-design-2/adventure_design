package com.example.myadventure.backend.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import com.example.myadventure.backend.mission.multimodal

// Ktor 클라이언트 설정
val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // 응답에서 알 수 없는 키 무시
            isLenient = true         // 느슨한 JSON 파싱 허용
        })
    }
}

// 미션 데이터 모델
@Serializable
data class Mission(
    val title: String,
    val location: String,
    val description: String
)

// Multimodal의 생성 결과를 서버에 전송하는 미션 생성 함수
suspend fun requestMissionCreation(): Mission? {
    return try {
        // Multimodal 함수에서 콘텐츠 생성 데이터 가져오기
        val generatedContent = multimodal()

        // 콘텐츠를 JSON 형식으로 변환
        val contentJson = Json.encodeToString(generatedContent)

        // 서버에 POST 요청을 보내고 응답을 Mission 객체로 파싱
        client.post("https://example.com/api/generate-mission") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(mapOf("content" to contentJson))
        }.body() // 응답을 Mission 객체로 변환하여 반환
    } catch (e: Exception) {
        e.printStackTrace()
        null // 실패 시 null 반환
    }
}
