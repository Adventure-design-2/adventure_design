package com.example.myadventure.backend.mission

import com.google.cloud.vertexai.VertexAI
import com.google.cloud.vertexai.api.GenerateContentResponse
import com.google.cloud.vertexai.api.GenerationConfig
import com.google.cloud.vertexai.api.HarmCategory
import com.google.cloud.vertexai.api.SafetySetting
import com.google.cloud.vertexai.generativeai.ContentMaker
import com.google.cloud.vertexai.generativeai.GenerativeModel
import com.google.cloud.vertexai.generativeai.ResponseStream
import java.io.IOException

fun multimodal(): List<String> {
    val responses = mutableListOf<String>()
    try {
        VertexAI("gen-lang-client-0976176955", "asia-northeast3").use { vertexAi ->
            val generationConfig = GenerationConfig.newBuilder()
                .setMaxOutputTokens(8192)
                .setTemperature(1F)
                .setTopP(0.95F)
                .build()

            val safetySettings = listOf(
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                    .build(),
                SafetySetting.newBuilder()
                    .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                    .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
                    .build()
            )

            val model = GenerativeModel.Builder()
                .apply {
                    setModelName("gemini-1.5-flash-002")
                    setVertexAi(vertexAi)
                    setGenerationConfig(generationConfig)
                    setSafetySettings(safetySettings)
                }
                .build()

            val content = ContentMaker.fromMultiModalData()
            val responseStream: ResponseStream<GenerateContentResponse> = model.generateContentStream(content)

            // Temporary inspection: Print the response to find the correct property
            responseStream.stream().forEach { response ->
                println(response) // Inspect the structure of the response object here
                // Try various properties like `response.content` or `response.getContent()`
                // responses.add(response.content) // Replace `content` with the correct property
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return responses
}
