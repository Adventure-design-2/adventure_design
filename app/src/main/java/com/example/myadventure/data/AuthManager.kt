@file:Suppress("DEPRECATION")

package com.example.myadventure.data

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 회원가입
    suspend fun registerUser(email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user
    }

    // 로그인
    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    // 로그아웃
    fun logoutUser() {
        auth.signOut()
    }

    // 현재 로그인된 사용자 가져오기
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Google Sign-In Client 설정
    fun getGoogleSignInClient(activity: Activity, webClientId: String): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    // Google 로그인 처리
    suspend fun firebaseAuthWithGoogle(idToken: String): GoogleSignInAccount? {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user?.let { GoogleSignIn.getLastSignedInAccount(auth.app.applicationContext) }
    }
}
