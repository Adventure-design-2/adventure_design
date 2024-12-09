package com.example.myadventure.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myadventure.data.InviteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class InviteViewModel : ViewModel() {
    private val repository = InviteRepository()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _inviteState = MutableStateFlow<String?>(null)
    val inviteState: StateFlow<String?> get() = _inviteState

    // 초대 코드 생성
    suspend fun generateInviteCode(): String {
        var isUnique = false
        var newCode: String

        do {
            newCode = InviteUtils.generateInviteCode()
            val existingCode = firestore.collection("users")
                .whereEqualTo("inviteCode", newCode)
                .get()
                .await()

            if (existingCode.isEmpty) {
                isUnique = true
            }
        } while (!isUnique)

        return newCode
    }

    object InviteUtils {
        private const val CODE_LENGTH = 6
        private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        // 12자 길이의 랜덤 초대 코드 생성
        fun generateInviteCode(): String {
            return (1..CODE_LENGTH)
                .map { Random.nextInt(0, CHAR_POOL.length) }
                .map(CHAR_POOL::get)
                .joinToString("")
        }
    }
    // 초대 코드 검증 및 연동
    fun validateInviteCode(uid: String, inviteCode: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val partnerUid = repository.validateAndLinkInviteCode(uid, inviteCode)
                if (partnerUid != null) {
                    onSuccess(partnerUid)
                } else {
                    onError("Invalid invite code")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun updateInviteState(userUid: String) {
        viewModelScope.launch {
            try {
                // Firestore에서 사용자 초대 코드 가져오기
                val userDocument = firestore.collection("users").document(userUid).get().await()
                val inviteCode = userDocument.getString("inviteCode") ?: ""

                // 상태 업데이트
                if (inviteCode.isNotEmpty()) {
                    _inviteState.value = inviteCode
                } else {
                    _inviteState.value = null // 초대 코드가 없을 경우 null로 설정
                }
            } catch (e: Exception) {
                // 오류 발생 시 로그 출력 및 상태 초기화
                e.printStackTrace()
                _inviteState.value = null
            }
        }
    }

}
