package com.example.myadventure.backend.mission

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json {
            ignoreUnknownKeys = true // Ignore unknown fields to avoid crashes
        })
    }
}

suspend fun fetchMissions(): List<Mission> {
    return withContext(Dispatchers.IO) {
        try {
            val response: HttpResponse = client.get("https://example.com/api/missions")
            val jsonString = response.readText()
            Json.decodeFromString<List<Mission>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
