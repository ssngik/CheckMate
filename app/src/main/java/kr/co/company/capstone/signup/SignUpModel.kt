package kr.co.company.capstone.signup

import android.content.Context
import kr.co.company.capstone.dto.login.LoginRequestKt
import kr.co.company.capstone.dto.login.LoginResponseKt
import kr.co.company.capstone.dto.login.SignUpRequest
import kr.co.company.capstone.service.MyFirebaseMessagingService
import kr.co.company.capstone.service.UserService
import kr.co.company.capstone.util.SharedPreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class SignUpModel : SignUpContract.Model {
    private lateinit var identifier : String
    override fun getUserSignUpInformation(context : Context, nickname: String) : SignUpRequest {
        // 사용자 식별 정보
        val providerId = SharedPreferenceUtil.getString(context, "providerId")
        identifier = UUID.nameUUIDFromBytes(providerId.toByteArray()).toString()
        val username = SharedPreferenceUtil.getString(context, "username")
        val emailAddress = SharedPreferenceUtil.getString(context, "emailAddress")

        return SignUpRequest(identifier, username, emailAddress, nickname)
    }

    // 로그인 필요 정보
    override fun getUserLoginInformation(): LoginRequestKt {
        val fcmToken = MyFirebaseMessagingService.fcmToken
        return LoginRequestKt(identifier, fcmToken)
    }

    // 회원가입
    override fun callSignUpApi(
        context : Context,
        signupRequest : SignUpRequest,
        onSuccess: (Boolean) -> Unit,
        onFailure : (String) -> Unit
    ) {
        UserService.getService().signUp(signupRequest).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onSuccess(response.isSuccessful)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure("문제가 발생 했습니다.")
            } })
    }

    // 로그인
    override fun callLoginApi(
        nickname : String,
        context : Context,
        onSuccess : (Boolean) -> Unit,
        onFailure : (String) -> Unit
    ) {
        val request = getUserLoginInformation()
        UserService.getService().login(request).enqueue(object : Callback<LoginResponseKt>{
            override fun onResponse(call: Call<LoginResponseKt>, response: Response<LoginResponseKt>) {
                if (response.isSuccessful){
                    val loginResponse = response.body()
                        val accessToken = loginResponse?.accessToken
                        val refreshToken = loginResponse?.refreshToken

                        SharedPreferenceUtil.setString(context, "accessToken", accessToken)
                        SharedPreferenceUtil.setString(context, "refresh Token", refreshToken)
                        SharedPreferenceUtil.setString(context, "nickname", nickname)
                        onSuccess(true)
                }else{
                    onFailure("로그인에 실패했습니다.")
                }
            }

            override fun onFailure(call: Call<LoginResponseKt>, t: Throwable) {
                onFailure("문제가 발생했습니다.")
            }
        })
    }

    // 닉네임 중복 체크
    override fun callNicknameDupCheckApi(
        nickname: String,
        onSuccess: (Boolean) -> Unit,
        onFailure : (String) -> Unit
    ){
        UserService.getService().duplicatedNicknameCheck(nickname)
            .enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    val isValid = response.isSuccessful && response.code() == 200
                    onSuccess(isValid)
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    onFailure("문제가 발생 했습니다.")
                } })
    }

}