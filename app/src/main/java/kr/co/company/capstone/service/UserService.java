package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.UserNicknameUpdateRequest;
import kr.co.company.capstone.dto.goal.GoalInfoListResponse;
import kr.co.company.capstone.dto.goal.HistoryGoalInfoResponse;
import kr.co.company.capstone.dto.login.UserSignUpRequest;
import kr.co.company.capstone.dto.login.UserSignUpResponse;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserService {
    @GET("/user/exists")
    Call<Void> duplicatedNicknameCheck(@Query("nickname") String nickname);

    @POST("/user")
    Call<UserSignUpResponse> SignUp(@Body UserSignUpRequest request);

    @PATCH("/user/nickname")
    Call<Void> updateNickname(@Body UserNicknameUpdateRequest request);

    @GET("/goal/history")
    Call<GoalInfoListResponse<HistoryGoalInfoResponse>> userHistory();

    static UserService getService(){
        return RetrofitBuilder.getRetrofit().create(UserService.class);
    }
}
