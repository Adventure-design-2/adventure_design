package com.example.myadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// UI 상태를 나타내는 sealed class 정의
sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val missionContents: List<Mission>) : UiState()
    data class Error(val message: String) : UiState()
}

// 미션 데이터 모델 정의
@Serializable
data class Mission(
    val title: String,
    val description: String,
    val location: String
)

class MissionViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()



    private val prompt =
        """당신은 "창의적인 커플 미션 생성기"입니다. 20~30대 커플을 위해 재미있고 관계를 돈독하게 해 줄 미션을 만들어야 합니다. 미션의 주된 목표는 커플들이 즐거움을 느끼고 관계를 강화하는 것입니다.  미션의 약 30%는 환경 보호와 관련되어야 하지만, 지루하거나 부가적인 활동이 아닌 재미있고 매력적인 방식으로 제시되어야 합니다.

다음 단계를 따라 미션을 생성하세요:

1. **미션 아이디어 생성:** 먼저 세 가지 미션 아이디어를 생각해냅니다.
2. **미션 다듬기:** 각 미션의 표현과 내용을 명확하고 매력적으로 다듬습니다.
3. **장소 정보 추가:** 각 미션을 수행하기에 적합한 장소를 구체적으로 명시하거나, 혹은 커플이 직접 선택할 수 있도록 가이드라인을 제시합니다. 
4. **비유적 제목:** 각 미션의 제목은 미션 내용을 흥미롭게 보여주는 비유적인 표현이어야 합니다. 전혀 관련 없어 보이는 비유라도 좋습니다. 한국어로만 작성한다. 
5. **JSON 출력:** 프론트엔드 인터페이스와 쉽게 통합될 수 있도록 JSON 파일 형식으로 응답을 생성합니다. 각 미션은 제목과 자세한 설명, 그리고 장소 정보를 포함해야 합니다.

아래 JSON 형식을 따르세요:

[
  {
    "title": "미션 1 제목 (비유적 표현)",
    "description": "미션 1에 대한 자세한 설명",
    "location": "미션 1을 수행하기에 적합한 장소 정보 또는 가이드라인"
  },
  {
    "title": "미션 2 제목 (비유적 표현)",
    "description": "미션 2에 대한 자세한 설명",
    "location": "미션 2을 수행하기에 적합한 장소 정보 또는 가이드라인"
  },
  {
    "title": "미션 3 제목 (비유적 표현)",
    "description": "미션 3에 대한 자세한 설명",
    "location": "미션 3을 수행하기에 적합한 장소 정보 또는 가이드라인"
  }
]

이제 20~30대 커플을 위한 재미있고 의미있는 미션을 만들어 보세요!
    """.trimIndent()

    // 미션 생성 요청
    fun createMissions() {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 생성 설정 정의
                val generationConfig = generationConfig {
                    temperature = 0.7f
                    topP = 0.9f
                    topK = 30
                    candidateCount = 1
                    maxOutputTokens = 500
                    stopSequences = listOf("in conclusion", "end")
                    responseMimeType = "application/json"
                }

                // Content 객체 생성
                val content = content {
                    text(prompt)
                }

                // GenerativeModel 생성 및 사용
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-pro-002",
                    apiKey = BuildConfig.API_KEY
                )

                val response = generativeModel.generateContent(content)
                var missionJson = response.text

                // 응답의 시작 부분에 잘못된 토큰이 포함되어 있는 경우 제거
                missionJson = missionJson?.replaceFirst("```json", "")?.trim() // `json` 문자열 제거
                missionJson = missionJson?.replaceFirst("```", "")?.trim() // 남아 있는 `` 제거

                // 응답 텍스트가 있는 경우 JSON 파싱
                missionJson?.let {
                    try {
                        // JSON 문자열을 List<Mission>으로 파싱
                        val missions: List<Mission> = Json.decodeFromString(missionJson)

                        // 성공적인 응답 처리
                        if (missions.isNotEmpty()) {
                            _uiState.value = UiState.Success(missions)
                        } else {
                            _uiState.value = UiState.Error("No missions were generated")
                        }
                    } catch (e: Exception) {
                        _uiState.value =
                            UiState.Error("Failed to parse mission response: ${e.localizedMessage}")
                    }
                } ?: run {
                    _uiState.value = UiState.Error("No content in response")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "An unknown error occurred")
            }
        }
    }
}