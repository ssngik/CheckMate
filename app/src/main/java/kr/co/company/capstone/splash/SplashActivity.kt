package kr.co.company.capstone.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.co.company.capstone.R
import kr.co.company.capstone.activity.LoginActivity
import kr.co.company.capstone.main.MainActivity

class SplashActivity : AppCompatActivity(), SplashContract.SplashView{
    private lateinit var presenter : SplashContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val model = SplashModel()
        presenter = SplashPresenter(this, model, applicationContext)
        presenter.checkUserStatus()
    }

    // 저장된 사용자 정보가 없는 경우 Login Activity로 이동
    override fun actionToLoginActivity() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Main Activity로 이동
    override fun actionToMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}