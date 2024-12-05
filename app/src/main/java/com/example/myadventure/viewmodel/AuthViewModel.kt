package com.example.myadventure.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myadventure.data.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch



sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userUid: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val authManager = AuthManager()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authManager.registerUser(email, password)
                _authState.value = if (user != null) AuthState.Success(user.uid) else AuthState.Error("Registration failed")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authManager.loginUser(email, password)
                _authState.value = if (user != null) AuthState.Success(user.uid) else AuthState.Error("Login failed")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logoutUser() {
        authManager.logoutUser()
        _authState.value = AuthState.Idle
    }
    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val account = authManager.firebaseAuthWithGoogle(idToken)
                if (account != null) {
                    onResult(true, account.email)
                } else {
                    onResult(false, "Google Sign-In failed")
                }
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
