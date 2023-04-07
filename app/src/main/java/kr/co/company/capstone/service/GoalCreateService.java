package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.GoalCreateRequest;
import kr.co.company.capstone.dto.goal.GoalCreateResponse;
import kr.co.company.capstone.dto.goal.GoalModifyRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GoalCreateService {
    // 목표 생성
    @POST("/goals")
    Call<GoalCreateResponse> saveGoal(@Body GoalCreateRequest goalCreateRequest);

    // 목표 수정
    @PATCH("/goals/{goalId}")
    Call<Void> modifyGoal(@Path("goalId") long goalId, @Body GoalModifyRequest goalModifyRequest);

    static GoalCreateService getService(){
        return RetrofitBuilder.getRetrofit().create(GoalCreateService.class);
    }
}
