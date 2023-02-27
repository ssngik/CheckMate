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
    @POST("/goal")
    Call<GoalCreateResponse> saveGoal(@Body GoalCreateRequest goalCreateRequest);

    @PATCH("/goal/{goalId}")
    Call<Void> modifyGoal(@Path("goalId") long goalId, @Body GoalModifyRequest goalModifyRequest);

    static GoalCreateService getService(){
        return RetrofitBuilder.getRetrofit().create(GoalCreateService.class);
    }
}
