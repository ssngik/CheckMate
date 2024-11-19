package kr.co.company.capstone.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor(private val accessToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        // 요청에 Authorization 추가
        val updateRequest = original.newBuilder().header("Authorization", accessToken).build()

        return chain.proceed(updateRequest)
    }

}
