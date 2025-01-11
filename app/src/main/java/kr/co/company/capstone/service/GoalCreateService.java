package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.MakeGoalResponse;
import kr.co.company.capstone.dto.goal.MakeGoalRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GoalCreateService {
    // 목표 생성
    @POST("/goals")
    Call<MakeGoalResponse> saveGoal(@Body MakeGoalRequest makeGoalRequest);

    static GoalCreateService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(GoalCreateService.class);
    }
}
