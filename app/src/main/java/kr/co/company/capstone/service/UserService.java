package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.login.LoginRequestKt;
import kr.co.company.capstone.dto.login.LoginResponse;
import kr.co.company.capstone.dto.login.SignUpRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {
    // 닉네임 중복 검사
    @GET("/users/exists")
    Call<Void> duplicatedNicknameCheck(@Query("nickname") String nickname);

    // 로그인
    @POST("/users/login")
    Call<LoginResponse> login(@Body LoginRequestKt loginRequestKt);

    // 회원가입
    @POST("/users")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);

    static UserService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(UserService.class);
    }
}
