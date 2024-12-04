package com.example.myadventure

import kotlin.random.Random

// A기기에서 인증코드를 생성
// 인증 코드 생성 함수
fun generateAuthCode(): String {
    return List(6) { Random.nextInt(10) }.joinToString("")  // 6자리 인증 코드 생성
}

// B기기에서 인증코드 입력 후 연동 확인
fun verifyAuthCode(inputCode: String, generatedCode: String): Boolean {
    return inputCode == generatedCode  // 입력한 코드가 생성된 코드와 일치하는지 확인
}

// 메인 연동 로직 함수
fun main() {
    // A, B 연동
    var isDevicesLinked = false
    var generatedCode: String? = null

    // 기기 A에서 코드 생성
    generatedCode = generateAuthCode()
    println("기기 A에서 생성된 인증 코드: $generatedCode")

    // 기기 B에서 코드 입력 받기 (예시: 사용자가 '123456'을 입력했다고 가정)
    val inputCode = "123456"  // 예시 입력값

    // 인증 코드 확인
    isDevicesLinked = verifyAuthCode(inputCode, generatedCode)

    if (isDevicesLinked) {
        println("기기 A와 B가 성공적으로 연동되었습니다. 같은 화면을 표시합니다.")
        // 연동된 화면 표시 로직 추가 (예: UI 업데이트)
    } else {
        println("인증 코드가 일치하지 않습니다. 다시 시도하세요.")
    }
}
