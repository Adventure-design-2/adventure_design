@file:Suppress("DEPRECATION")

package com.example.myadventure.data

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 이메일과 비밀번호로 로그인
    suspend fun loginWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result: AuthResult = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null // 실패 시 null 반환
        }
    }

    // 이메일과 비밀번호로 회원가입
    suspend fun registerWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result: AuthResult = auth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            null // 실패 시 null 반환
        }
    }

    // Google 로그인
    suspend fun loginWithGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result: AuthResult = auth.signInWithCredential(credential).await()
            result.user
        } catch (e: Exception) {
            null // 실패 시 null 반환
        }
    }

    // GoogleSignInClient 반환
    fun getGoogleSignInClient(context: Context, webClientId: String): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    // Google 로그인 처리
    suspend fun firebaseAuthWithGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result: AuthResult = auth.signInWithCredential(credential).await()
            result.user
        } catch (e: Exception) {
            null
        }
    }

    // 로그아웃
    fun logout() {
        auth.signOut()
    }

    // 현재 사용자 가져오기
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
