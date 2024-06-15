package kr.co.company.capstone.service

import kr.co.company.capstone.BuildConfig
import kr.co.company.capstone.GlobalApplication
import kr.co.company.capstone.interceptor.TokenAuthenticator
import kr.co.company.capstone.interceptor.TokenInterceptor
import kr.co.company.capstone.util.SharedPreferenceUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitBuilder {

    val retrofit: Retrofit by lazy{
        buildRetrofit()
    }

    private fun buildRetrofit(): Retrofit{
        val builder = OkHttpClient.Builder()
        val accessToken = SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "accessToken")
        builder.addInterceptor(TokenInterceptor(accessToken))
        builder.authenticator(TokenAuthenticator())
        val client = builder.build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
