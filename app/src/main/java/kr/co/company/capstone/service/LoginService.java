package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.login.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/login/reissue")
    Call<LoginResponse> reissue(@Body TokenReissueRequest tokenReissueRequest);

    // 로그인
    @POST("/users/login")
    Call<LoginResponse> login(@Body LoginRequestKt loginRequestKt);

    static LoginService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(LoginService.class);
    }
}
