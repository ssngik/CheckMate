package kr.co.company.capstone.dto.login

data class TokenReissueRequest (
    val accessToken: String,
    val refreshToken: String
)