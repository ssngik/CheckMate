package kr.co.company.capstone.login

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kr.co.company.capstone.BuildConfig
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.login.LoginRequestKt
import kr.co.company.capstone.dto.login.LoginResponseKt
import kr.co.company.capstone.service.LoginService
import kr.co.company.capstone.service.MyFirebaseMessagingService
import kr.co.company.capstone.util.SharedPreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class LoginModel(private val context: Context) : LoginContract.LoginModel {
    private lateinit var loginRequest: LoginRequestKt
    override fun callNaverLoginApi(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val oAuthClientId = BuildConfig.OAUTH_CLIENT_ID
        val oAuthClientSecret = BuildConfig.OAUTH_CLIENT_SECRET
        val oAuthClientName = BuildConfig.OAUTH_CLIENT_NAME

        // 네이버 로그인 초기화
        NaverIdLoginSDK.initialize(context, oAuthClientId, oAuthClientSecret, oAuthClientName)

        // 네이버 로그인 콜백
        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                var identifier = SharedPreferenceUtil.getString(context, "identifier")
                // 신규 회원인 경우
                if (identifier.isEmpty()) {
                    // 회원가입에 필요한 정보
                    val providerId = result.profile?.id
                    identifier = UUID.nameUUIDFromBytes(providerId?.toByteArray()).toString()
                    SharedPreferenceUtil.setString(context, "identifier", identifier)
                    SharedPreferenceUtil.setString(context, "username", result.profile?.name)
                    SharedPreferenceUtil.setString(context, "emailAddress", result.profile?.email)
                }
                onSuccess()
                SharedPreferenceUtil.setString(context, "social", "naver")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                onFailure(message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(profileCallback)
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(message)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                onFailure(message)
            }

        }
        NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
    }

    // 카카오 로그인 API
    override fun callKakaoLoginApi(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        KakaoSdk.init(context, BuildConfig.KAKAO_NATIVE_KEY)

        // kakao 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = initKakaoCallback(onSuccess)

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->

                if (error != null) {
                    // 카카오톡으로 로그인 실패
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }
    // 카카오 로그인 콜백 초기화
    private fun initKakaoCallback(onSuccess: () -> Unit): (OAuthToken?, Throwable?) -> Unit {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                // 카카오계정으로 로그인 실패
            } else if (token != null) {
                // 카카오 계정으로 로그인 성공
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        // 사용자 정보 요청 실패
                    } else if (user != null) {
                        val providerId = user.id.toString()
                        val identifier = UUID.nameUUIDFromBytes(providerId.toByteArray()).toString()

                        SharedPreferenceUtil.setString(context, "identifier", identifier)
                        SharedPreferenceUtil.setString(context, "username", user.kakaoAccount?.profile?.nickname)
                        SharedPreferenceUtil.setString(context, "email", user.kakaoAccount?.email!!)


                        onSuccess()
                        SharedPreferenceUtil.setString(context, "social", "kakao")
                    }
                }

            }
        }
        return callback
    }

    // 서버에 로그인 API 호출
    override fun callLoginApi(onSuccess: () -> Unit, onFailure: (String) -> Unit) {

        val identifier = SharedPreferenceUtil.getString(context, "identifier")
        loginRequest = LoginRequestKt(identifier, MyFirebaseMessagingService.fcmToken)
        LoginService.getService().login(loginRequest).enqueue(object : Callback<LoginResponseKt> {
            override fun onResponse(
                call: Call<LoginResponseKt>,
                response: Response<LoginResponseKt>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    SharedPreferenceUtil.setString(context, "accessToken", loginResponse?.accessToken)
                    SharedPreferenceUtil.setString(context, "refreshToken", loginResponse?.refreshToken)

                    onSuccess()
                } else if (ErrorMessage.getErrorByResponse(response).code == "USER-001"){
                    // 존재하지 않는 유저인 경우
                    onFailure("USER-001")
                }
            }

            override fun onFailure(call: Call<LoginResponseKt>, t: Throwable) {
                onFailure("문제가 발생했습니다.")
            }
        })
    }
}