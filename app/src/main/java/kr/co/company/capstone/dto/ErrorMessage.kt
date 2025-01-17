package kr.co.company.capstone.dto

import com.google.gson.Gson
import retrofit2.Response

data class ErrorMessage(
    val timestamp: String? = null,
    val status: Int = 0,
    val error: String? = null,
    val code: String? = null,
    val message: String? = null
) {
    companion object {
        fun getErrorByResponse(response: Response<*>): ErrorMessage {
            response.errorBody()?.charStream()?.let {
                return Gson().fromJson(it, ErrorMessage::class.java)
            }
            throw IllegalArgumentException("Invalid Response error")
        }
    }
}