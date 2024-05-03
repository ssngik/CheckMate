package kr.co.company.capstone.dto.login


data class SignUpRequest(
    private val identifier: String,
    private val username: String,
    private val emailAddress: String,
    private val nickname: String
)