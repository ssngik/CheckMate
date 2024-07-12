package kr.co.company.capstone.login


class LoginPresenter(
    private var view : LoginContract.LoginView,
    private val model : LoginContract.LoginModel
) : LoginContract.LoginPresenter {

    // 네이버 로그인 버튼 클릭
    override fun onNaverLoginButtonClicked() {
        model.callNaverLoginApi(
            onSuccess = { callLoginApi() },
            onFailure = { message -> view.showErrorDialog(message) }
        )
    }

    // 카카오 로그인 버튼 클릭
    override fun onKakaoLoginButtonClicked() {
        model.callKakaoLoginApi(
            onSuccess = { callLoginApi() },
            onFailure = { message -> view.showErrorDialog(message)}
        )
    }
    // 사용자 로그인
    private fun callLoginApi() {
        model.callLoginApi(
            onSuccess = {
                view.actionToMainActivity()},
            onFailure = { error ->
                // 존재하지 않는 유저 : 회원가입 진행
                if (error == "USER-001"){
                    view.actionToSignupActivity()
                }else{
                    view.showErrorDialog(error)
                }
            }
        )
    }
}