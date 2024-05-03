package kr.co.company.capstone.signup

import android.content.Context
import kr.co.company.capstone.dto.login.LoginRequestKt
import kr.co.company.capstone.dto.login.SignUpRequest

interface SignUpContract {
    interface SignUpView{
        fun showProgress()
        fun hideProgress()
        fun actionToMainActivity()
        fun setNicknameValidityDrawable(isValid : Boolean)
        fun showNicknameValidityDialog(isAvailable: Boolean)
        fun showErrorDialog(errorMessage : String)
    }
    interface Presenter{
        fun onJoinButtonClicked(nickname : String)
        fun getNicknameCheckDrawable(response: SignUpPresenter.NicknameCheckCode): Int
        fun checkNicknameValidity(nickname : String) : SignUpPresenter.NicknameCheckCode
        fun checkNicknameDuplicate(nickname : String)
        fun actionToMainActivity()
    }

    interface Model{
        fun getUserSignUpInformation(context : Context, nickname: String) : SignUpRequest
        fun getUserLoginInformation() : LoginRequestKt
        fun callSignUpApi(
            context : Context,
            signupRequest : SignUpRequest,
            onSuccess : (Boolean) -> Unit,
            onFailure : (String) -> Unit
        )
        fun callLoginApi(
            nickname: String,
            context : Context,
            onSuccess : (Boolean) -> Unit,
            onFailure : (String) -> Unit
        )
        fun callNicknameDupCheckApi(
            nickname : String,
            onSuccess : (Boolean) -> Unit,
            onFailure : (String) -> Unit
        )

    }
}