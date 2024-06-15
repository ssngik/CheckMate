package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.UserNicknameUpdateRequest;
import kr.co.company.capstone.dto.login.LoginRequest;
import kr.co.company.capstone.dto.login.LoginRequestKt;
import kr.co.company.capstone.dto.login.LoginResponse;
import kr.co.company.capstone.dto.login.LoginResponseKt;
import kr.co.company.capstone.dto.login.SignUpRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {
    // 닉네임 중복 검사
    @GET("/users/exists")
    Call<Void> duplicatedNicknameCheck(@Query("nickname") String nickname);

    // 로그인
    @POST("/users/login")
    Call<LoginResponseKt> login(@Body LoginRequestKt loginRequestKt);

    // 로그인_자바
    @POST("/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // 회원가입
    @POST("/users")
    Call<Void> signUp(@Body SignUpRequest signUpRequest);

    // 닉네임 변경
    @PATCH("/users/nickname")
    Call<Void> updateNickname(@Body UserNicknameUpdateRequest request);


    static UserService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(UserService.class);
    }
}
