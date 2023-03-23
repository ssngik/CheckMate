package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.UserNicknameUpdateRequest;
import kr.co.company.capstone.dto.goal.GoalInfoListResponse;
import kr.co.company.capstone.dto.goal.HistoryGoalInfoResponse;
import kr.co.company.capstone.dto.login.UserSignUpRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {
    @GET("/users/exists")
    Call<Void> duplicatedNicknameCheck(@Query("nickname") String nickname);

    @POST("/users/kakao") // LoginResponse
    Call<Void> KakaoSignUp(@Body UserSignUpRequest request);

//    @POST("/users/naver")
//    Call<LoginResponse> SignUp(@Body UserSignUpRequest request);
//
//    @POST("/users/google")
//    Call<LoginResponse> SignUp(@Body UserSignUpRequest request);

    @PATCH("/users/nickname")
    Call<Void> updateNickname(@Body UserNicknameUpdateRequest request);

    @GET("/goal/history")
    Call<GoalInfoListResponse<HistoryGoalInfoResponse>> userHistory();

    static UserService getService(){
        return RetrofitBuilder.getRetrofit().create(UserService.class);
    }
}
