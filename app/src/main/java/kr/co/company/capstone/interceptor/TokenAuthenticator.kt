package kr.co.company.capstone.interceptor

import android.content.Intent
import android.os.Handler
import android.os.Looper
import kr.co.company.capstone.BuildConfig
import kr.co.company.capstone.GlobalApplication
import kr.co.company.capstone.dto.login.TokenReissueRequest
import kr.co.company.capstone.login.LoginActivity
import kr.co.company.capstone.service.LoginService
import kr.co.company.capstone.util.SharedPreferenceUtil
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code == 401) { // 401 응답을 받은 경우
            return if (refreshToken()) {
                /// 재발급 성공시 새로운 access token
                response.request.newBuilder()
                    .header(
                        "Authorization",
                        SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "accessToken")
                    )
                    .build()
            } else {
                redirectToLoginActivity() // 재발급 실패시 로그인 화면 redirect
                null // 요청 중단
            }
        }
        return null
    }

    // 토큰 재발급
    private fun refreshToken(): Boolean {
        return try {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val accessToken = SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "accessToken")
                .replace("Bearer ", "")
            val refreshToken = SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "refreshToken")
                .replace("Bearer ", "")

            val tokenReissueRequest = TokenReissueRequest(accessToken, refreshToken)

            // 토큰 재발급 API 호출
            val result = retrofit.create(LoginService::class.java).reissue(tokenReissueRequest).execute()

            if (result.isSuccessful) {
                result.body()?.let { loginResponse -> // 새로운 토큰 저장
                    SharedPreferenceUtil.setString(
                        GlobalApplication.getAppContext(),
                        "accessToken",
                        loginResponse.accessToken
                    )
                    SharedPreferenceUtil.setString(
                        GlobalApplication.getAppContext(),
                        "refreshToken",
                        loginResponse.refreshToken
                    )
                    true
                } ?: false // 실패 : 바디가 없음
            } else {
                false // 재발급 실패인 경우
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    // 로그인 화면 redirect
    private fun redirectToLoginActivity() {
        val intent = Intent(GlobalApplication.getAppContext(), LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        Handler(Looper.getMainLooper()).post {
            GlobalApplication.getAppContext().startActivity(intent)
        }
    }
}
