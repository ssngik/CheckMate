package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.login.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/login/reissue")
    Call<LoginResponse> reissue(@Body TokenReissueRequest tokenReissueRequest);

    @DELETE("/users/logout")
    Call<Void> logout();

    // 로그인
    @POST("/users/login")
    Call<LoginResponseKt> login(@Body LoginRequestKt loginRequestKt);

    static LoginService getService(){
        return RetrofitBuilder.getRetrofit().create(LoginService.class);
    }
}
