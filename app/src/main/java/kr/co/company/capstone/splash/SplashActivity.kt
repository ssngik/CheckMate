package kr.co.company.capstone.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.co.company.capstone.R
import kr.co.company.capstone.login.LoginActivity
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
    }

    // Main Activity로 이동
    override fun actionToMainActivity() {
        val messageBody = intent.getStringExtra("messageBody")
        val notificationId = intent.getLongExtra("notificationId", 0L)
        val goalId = intent.getLongExtra("goalId", 0L)
        val userId = intent.getLongExtra("userId", 0L)
        val navigateTo = intent.getStringExtra("navigateTo")

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("messageBody", messageBody)
            putExtra("notificationId", notificationId)
            putExtra("goalId", goalId)
            putExtra("userId", userId)
            putExtra("navigateTo", navigateTo)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        startActivity(intent)
    }
}