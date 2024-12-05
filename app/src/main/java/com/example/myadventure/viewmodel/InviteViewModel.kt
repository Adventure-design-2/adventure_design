package com.example.myadventure.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myadventure.data.InviteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InviteViewModel : ViewModel() {
    private val repository = InviteRepository()

    private val _inviteState = MutableStateFlow<String?>(null)
    val inviteState: StateFlow<String?> get() = _inviteState

    // 초대 코드 생성
    fun generateInviteCode(uid: String) {
        viewModelScope.launch {
            try {
                val code = repository.generateInviteCode(uid)
                _inviteState.value = code
            } catch (e: Exception) {
                _inviteState.value = null // 오류 발생 시 null 반환
            }
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
}
