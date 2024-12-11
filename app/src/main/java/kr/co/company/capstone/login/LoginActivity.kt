package kr.co.company.capstone.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.co.company.capstone.databinding.ActivityLoginBinding
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.main.MainActivity
import kr.co.company.capstone.service.FirebaseMessagingService
import kr.co.company.capstone.signup.SignUpActivity

class LoginActivity : AppCompatActivity(), LoginContract.LoginView {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : LoginContract.LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = LoginPresenter(this, LoginModel(this))
        startFcmTokenService()
        initListener()
    }

    // 로그인 버튼 클릭 리스너
    private fun initListener() {
        binding.naverLoginBtn.setOnClickListener { presenter.onNaverLoginButtonClicked() }
        binding.kakaoLoginBtn.setOnClickListener { presenter.onKakaoLoginButtonClicked() }
    }

    // FCM Token 서비스 실행
    private fun startFcmTokenService() {
        val intent = Intent(this, FirebaseMessagingService::class.java)
        startService(intent)
    }

    // 회원가입 이동
    override fun actionToSignupActivity(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 메인화면 이동
    override fun actionToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showErrorDialog(errorMessage : String) {
        ErrorDialogFragment.getErrorMessage(errorMessage).show(supportFragmentManager, "error_dialog")
    }

}
