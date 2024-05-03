package kr.co.company.capstone.dto.login

data class LoginRequestKt(
    val identifier : String, // 로그인 시도하는 유저 식별자
    val fcmToken : String // 로그인한 기기의 fcmToken
)

