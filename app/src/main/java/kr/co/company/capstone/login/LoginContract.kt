package kr.co.company.capstone.login

interface LoginContract {
    interface LoginView{
        fun actionToSignupActivity()
        fun actionToMainActivity()
        fun showErrorDialog(errorMessage : String)
    }
    interface LoginPresenter{
        fun onNaverLoginButtonClicked()
        fun onKakaoLoginButtonClicked()
    }
    interface LoginModel{
        fun callNaverLoginApi(
            onSuccess : () -> Unit,
            onFailure : (String) -> Unit
        )
        fun callKakaoLoginApi(
            onSuccess : () -> Unit,
            onFailure : (String) -> Unit
        )
        fun callLoginApi(
            onSuccess : () -> Unit,
            onFailure : (String) -> Unit
        )
    }
}