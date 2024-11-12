package com.example.myadventure.backend.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.features.contentnegotiation.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

// 미션 생성 요청 함수
suspend fun requestMissionCreation(): Mission? {
    return try {
        // 서버에 POST 요청을 보내고 미션 생성 요청
        val response: Mission = client.post("https://example.com/api/generate-mission") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            // 필요하면 요청 본문에 데이터 추가
            setBody(mapOf("type" to "couple", "difficulty" to "medium"))
        }
        response // 성공 시 생성된 미션 반환
    } catch (e: Exception) {
        e.printStackTrace()
        null // 실패 시 null 반환
    }
}
