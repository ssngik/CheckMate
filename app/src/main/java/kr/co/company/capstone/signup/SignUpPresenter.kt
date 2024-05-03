package kr.co.company.capstone.signup

import android.content.Context
import kr.co.company.capstone.R
import java.util.regex.Pattern

class SignUpPresenter (
    private val signUpView : SignUpContract.SignUpView,
    private val model : SignUpContract.Model,
    private val context: Context ) : SignUpContract.Presenter {

    // 회원가입 버튼 클릭
    override fun onJoinButtonClicked(nickname : String) { callSignUpApi(nickname) }

    // 회원가입 API 요청
    private fun callSignUpApi(nickname: String) {
        signUpView.showProgress()
        val request = model.getUserSignUpInformation(context, nickname)
        model.callSignUpApi(context, request,
            onSuccess = {
                    result ->
                if (result) {
                    callLoginApi(nickname)
                    signUpView.hideProgress()
                }},
            onFailure = { error ->
                    signUpView.showErrorDialog(error)
                    signUpView.hideProgress()
            })
    }

    // 회원가입 성공 후 로그인
    private fun callLoginApi(nickname: String) {
        signUpView.showProgress()
        model.callLoginApi(nickname, context,
            onSuccess = {success ->
                if (success){
                    signUpView.hideProgress()
                    actionToMainActivity()
                }else{
                    signUpView.hideProgress()
                    signUpView.showErrorDialog("로그인에 실패했습니다.")
                }
            }, onFailure = {
                error ->
                signUpView.hideProgress()
                signUpView.showErrorDialog(error)
            }
        )
    }

    enum class NicknameCheckCode {
        INVALID_LENGTH_DETECTED,
        SPECIAL_CHARACTER_DETECTED,
        NICKNAME_OK
    }

    // 닉네임 체크 결과에 따른 Drawable Id 결과 반환
    override fun getNicknameCheckDrawable(code: NicknameCheckCode): Int{
        return when (code){
            NicknameCheckCode.NICKNAME_OK -> R.drawable.nickname_check_ok
            NicknameCheckCode.SPECIAL_CHARACTER_DETECTED -> R.drawable.notice_invalid_symbol
            NicknameCheckCode.INVALID_LENGTH_DETECTED -> R.drawable.notice_invalid_length
        }
    }

    // 특수문자 조건, 닉네임 길이 제한에 부합하는지
    override fun checkNicknameValidity(nickname: String): NicknameCheckCode {
        val charPattern = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$")
        val lengthPattern = Pattern.compile("^.{2,8}$")
        return if (!lengthPattern.matcher(nickname).matches()) NicknameCheckCode.INVALID_LENGTH_DETECTED
        else if (!charPattern.matcher(nickname).matches()) NicknameCheckCode.SPECIAL_CHARACTER_DETECTED
        else NicknameCheckCode.NICKNAME_OK
    }

    // 중복 닉네임 체크 메서드
    override fun checkNicknameDuplicate(nickname : String) {
        signUpView.showProgress()
        model.callNicknameDupCheckApi(nickname,
            onSuccess = { isValid ->
                signUpView.hideProgress()
                signUpView.showNicknameValidityDialog(isValid)
                signUpView.setNicknameValidityDrawable(isValid)
            },
            onFailure = { error ->
                signUpView.hideProgress()
                signUpView.showErrorDialog(error)
            }
        )
    }
    override fun actionToMainActivity() { signUpView.actionToMainActivity() }
}