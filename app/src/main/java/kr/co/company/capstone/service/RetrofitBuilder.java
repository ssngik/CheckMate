package kr.co.company.capstone.service;

import kr.co.company.capstone.BuildConfig;
import kr.co.company.capstone.GlobalApplication;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.interceptor.TokenInterceptor;
import kr.co.company.capstone.interceptor.TokenAuthenticator;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitBuilder {

     static Retrofit getRetrofit() {
         OkHttpClient.Builder builder = new OkHttpClient.Builder();
         String accessToken = SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "accessToken");
         builder.interceptors().add(new TokenInterceptor(accessToken));
         builder.authenticator(new TokenAuthenticator());
         OkHttpClient client = builder.build();


         return new Retrofit.Builder()
                 .baseUrl(BuildConfig.baseUrl)
                 .client(client)
                 .addConverterFactory(ScalarsConverterFactory.create())
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                 .addConverterFactory(GsonConverterFactory.create())
                 .build();
     }
}
