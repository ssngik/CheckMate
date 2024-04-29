package kr.co.company.capstone.splash

import android.content.Context
import android.os.Handler

class SplashPresenter(
    private val view: SplashContract.SplashView,
    private val model: SplashContract.Model,
    private val context: Context
) : SplashContract.Presenter{

    private val splashDelay = 2000L
    override fun checkUserStatus() {
        val accessToken = model.getAccessToken(context)
        Handler().postDelayed({
            if (accessToken.isNullOrEmpty()) {
                view.actionToLoginActivity()
            }
            else {
                view.actionToMainActivity()
            }
        }, splashDelay)
    }

}