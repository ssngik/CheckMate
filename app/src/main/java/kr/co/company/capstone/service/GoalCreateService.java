package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.MakeGoalResponse;
import kr.co.company.capstone.dto.goal.GoalModifyRequest;
import kr.co.company.capstone.dto.goal.MakeGoalRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GoalCreateService {
    // 목표 생성
    @POST("/goals")
    Call<MakeGoalResponse> saveGoal(@Body MakeGoalRequest makeGoalRequest);

    // 목표 수정
    @PATCH("/goals/{goalId}")
    Call<Void> modifyGoal(@Path("goalId") long goalId, @Body GoalModifyRequest goalModifyRequest);

    static GoalCreateService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(GoalCreateService.class);
    }
}
