package kr.co.company.capstone.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.co.company.capstone.R
import kr.co.company.capstone.Theme
import kr.co.company.capstone.util.SharedPreferenceUtil

class SplashActivity : AppCompatActivity() {
    private val splashDelay = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // accessToken 가져오기
        val accessToken = SharedPreferenceUtil.getString(this, "accessToken")
        //로딩화면 시작.
        LoadingStart(accessToken)
    }

    private fun LoadingStart(accessToken: String) {
        Handler().postDelayed({
            if (accessToken.isEmpty()) { // accessToken 만료
                navigateToLoginActivity()
            } else {
                navigateToMainActivity()
            }
            finish()
        }, splashDelay)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLoginActivity() {
        SharedPreferenceUtil.removeKey(applicationContext, "accessToken")
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
    }
}