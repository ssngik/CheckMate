package kr.co.company.capstone.dto.login

data class LoginRequestKt(
    var identifier : String, // 로그인 시도하는 유저 식별자
    var fcmToken : String // 로그인한 기기의 fcmToken
)

