package kr.co.company.capstone.interceptor;

import android.content.Intent;

import kr.co.company.capstone.BuildConfig;
import kr.co.company.capstone.GlobalApplication;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.login.LoginActivity;
import kr.co.company.capstone.dto.login.LoginResponse;
import kr.co.company.capstone.dto.login.TokenReissueRequest;
import kr.co.company.capstone.service.LoginService;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

public class TokenAuthenticator implements Authenticator {
    private static final String LOG_TAG = "TokenAuthenticator";

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, Response response) throws IOException {

        if (response.code() == 401) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            String accessToken = SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "accessToken").replace("Bearer ", "");
            String refreshToken = SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "refreshToken").replace("Bearer ", "");

            TokenReissueRequest tokenReissueRequest = TokenReissueRequest.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            retrofit2.Response<LoginResponse> result = retrofit.create(LoginService.class).reissue(tokenReissueRequest).execute();
            if(result.isSuccessful()) {
                LoginResponse loginResponse = result.body();
                SharedPreferenceUtil.setString(GlobalApplication.getAppContext(), "accessToken", loginResponse.getAccessToken());
                SharedPreferenceUtil.setString(GlobalApplication.getAppContext(), "refreshToken", loginResponse.getRefreshToken());

                return response.request().newBuilder()
                        .header("Authorization", SharedPreferenceUtil.getString(GlobalApplication.getAppContext(), "accessToken"))
                        .build();
            }
            else{
                Intent intent = new Intent(GlobalApplication.getAppContext(), LoginActivity.class);
                GlobalApplication.getAppContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        return null;
    }
}
