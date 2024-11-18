package com.example.myadventure.backend.mission

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Ktor 클라이언트 설정
val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true // 알 수 없는 필드는 무시
            isLenient = true         // 느슨한 JSON 파싱 허용
        })
    }
}

// 미션 데이터 모델 정의 (서버의 응답에 맞게 수정)
@Serializable
data class Mission(
    val title: String,
    val location: String,
    val description: String
)

// 미션 목록을 서버에서 가져오는 함수
suspend fun fetchMissions(): List<Mission> {
    return withContext(Dispatchers.IO) {
        try {
            // 서버에 GET 요청을 보내고, 응답을 List<Mission> 형식으로 파싱
            client.get("https://example.com/api/missions").body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // 실패 시 빈 리스트 반환
        }
    }
}
