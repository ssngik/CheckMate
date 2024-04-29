package kr.co.company.capstone.splash

import android.content.Context
import kr.co.company.capstone.util.SharedPreferenceUtil

class SplashModel() : SplashContract.Model {
    override fun getAccessToken(context: Context): String? {
        return SharedPreferenceUtil.getString(context, "accessToken")
    }
}