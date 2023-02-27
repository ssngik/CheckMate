package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.login.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/login/naver")
    Call<LoginResponse> naver(@Body NaverAuthModel naverAuthModel);

    @POST("/login/kakao")
    Call<LoginResponse> kakao(@Body KakaoAuthModel kakaoAuthModel);

    @POST("/login/google")
    Call<LoginResponse> google(@Body GoogleAuthModel googleAuthModel);

    @POST("/login/reissue")
    Call<LoginResponse> reissue(@Body TokenReissueRequest tokenReissueRequest);

    @DELETE("/login/logout")
    Call<Void> logout();

    static LoginService getService(){
        return RetrofitBuilder.getRetrofit().create(LoginService.class);
    }
}
