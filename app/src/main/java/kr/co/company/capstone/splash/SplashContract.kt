package kr.co.company.capstone.splash

import android.content.Context

interface SplashContract {
    interface SplashView{
        fun actionToLoginActivity()
        fun actionToMainActivity()
    }

    interface Presenter{
        fun checkUserStatus()
    }

    interface Model{
        fun getAccessToken(context : Context) : String?
    }
}