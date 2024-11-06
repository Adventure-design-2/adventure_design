package com.example.myadventure.ui.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserPreferences(context: Context) {

    private val appContext: Context = context.applicationContext

    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
        private const val USER_NAME_KEY = "user_name"
        private const val PROFILE_IMAGE_URI_KEY = "profile_image_uri"
        private const val USER_POINTS_KEY = "user_points"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private val sharedPreferences: SharedPreferences =
        appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    // 사용자 이름을 관찰하는 Flow
    val userNameFlow: Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == USER_NAME_KEY) {
                trySend(getUserName())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(getUserName()) // 초기값 전송
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    // 프로필 이미지 URI를 관찰하는 Flow
    val profileImageUriFlow: Flow<String?> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PROFILE_IMAGE_URI_KEY) {
                trySend(getProfileImageUri())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(getProfileImageUri()) // 초기값 전송
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    // 사용자 이름을 불러오는 함수
    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME_KEY, "사용자 이름") ?: "사용자 이름"
    }

    // 사용자 이름을 저장하는 함수
    fun saveUserName(userName: String) {
        sharedPreferences.edit {
            putString(USER_NAME_KEY, userName)
        }
    }

    // 프로필 이미지 URI를 불러오는 함수
    fun getProfileImageUri(): String? {
        return sharedPreferences.getString(PROFILE_IMAGE_URI_KEY, null)
    }

    // 프로필 이미지 URI를 저장하는 함수
    fun saveProfileImageUri(uri: String) {
        sharedPreferences.edit {
            putString(PROFILE_IMAGE_URI_KEY, uri)
        }
    }

    // 포인트를 관찰하는 Flow
    val pointsFlow: Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == USER_POINTS_KEY) {
                trySend(getPoints())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        trySend(getPoints()) // 초기값 전송
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    // 포인트를 불러오는 함수
    fun getPoints(): Int {
        return sharedPreferences.getInt(USER_POINTS_KEY, 0)
    }

    // 포인트를 저장하는 함수
    fun savePoints(points: Int) {
        sharedPreferences.edit {
            putInt(USER_POINTS_KEY, points)
        }
    }
}
