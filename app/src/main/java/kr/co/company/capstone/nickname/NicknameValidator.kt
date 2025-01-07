package kr.co.company.capstone.nickname

import kr.co.company.capstone.R
import java.util.regex.Pattern

object NicknameValidator {
    private val charPattern = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$")
    private val lengthPattern = Pattern.compile("^.{2,8}$")

    fun isValidNickname(nickname: String): Boolean {
        return lengthPattern.matcher(nickname).matches() &&
                charPattern.matcher(nickname).matches()
    }

    fun getValidationDrawable(nickname: String): Int {
        return when {
            !lengthPattern.matcher(nickname).matches() -> R.drawable.notice_invalid_length
            !charPattern.matcher(nickname).matches() -> R.drawable.notice_invalid_symbol
            else -> R.drawable.nickname_check_ok
        }
    }
}