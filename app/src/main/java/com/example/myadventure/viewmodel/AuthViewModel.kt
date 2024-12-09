package com.example.myadventure.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myadventure.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState()
    data object NotAuthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    init {
        checkAuthState()
    }

    // 현재 사용자 상태 확인
    fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                _authState.value = AuthState.Authenticated(currentUser)
            } else {
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }

    // 이메일과 비밀번호로 로그인
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading // 로딩 상태로 설정
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    _authState.value = AuthState.Success // 성공 시 상태 업데이트
                } else {
                    _authState.value = AuthState.Error("로그인 실패")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "로그인 중 오류 발생")
            }
        }
    }



    // 이메일과 비밀번호로 회원가입
    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean) -> Unit = {}) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModelScope.launch {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkLoginStatus() // 로그인 상태 업데이트
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }
        }
    }
    // 로그인 상태 확인
    private fun checkLoginStatus() {
        _isLoggedIn.value = auth.currentUser != null
    }


    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _authState.value = AuthState.NotAuthenticated // 로그아웃 후 상태 설정
                _isLoggedIn.value = false
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Logout failed")
            }
        }
    }



    // 현재 사용자 UID 반환
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    // 유저 프로필 로드
    fun loadUserProfile(onResult: (UserProfile?) -> Unit) {
        val userId = getCurrentUserId()
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val profile = document.toObject(UserProfile::class.java)
                        onResult(profile)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    onResult(null)
                }
        } else {
            onResult(null)
        }
    }

    fun saveUserProfile(profile: UserProfile, onResult: (Boolean) -> Unit) {
        val userId = getCurrentUserId()
        if (userId.isNotEmpty()) {
            firestore.collection("users").document(userId)
                .set(profile)
                .addOnSuccessListener {
                    onResult(true)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    onResult(false)
                }
        } else {
            onResult(false)
        }
    }
    fun updateInviteCode(userUid: String, newInviteCode: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val userRef = firestore.collection("users").document(userUid)
                userRef.update("inviteCode", newInviteCode).await()
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    suspend fun connectPartner(userUid: String, partnerCode: String): Boolean {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("inviteCode", partnerCode)
                .get()
                .await()

            if (snapshot.documents.isEmpty()) {
                return false
            }

            // 첫 번째 문서에서 이름과 UID 가져오기
            val partnerDocument = snapshot.documents.firstOrNull()
            val partnerName = partnerDocument?.getString("name") ?: ""
            val partnerUid = partnerDocument?.id ?: ""

            if (partnerUid.isEmpty()) {
                return false
            }

            // 사용자 프로필 업데이트
            firestore.collection("users").document(userUid)
                .update(
                    mapOf(
                        "partnerName" to partnerName,
                        "partnerUid" to partnerUid
                    )
                )
                .await()

            // 상대방 프로필에도 업데이트 (현재 사용자의 UID를 partnerUid로 설정)
            firestore.collection("users").document(partnerUid)
                .update(
                    mapOf(
                        "partnerUid" to userUid
                    )
                )
                .await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }




}
