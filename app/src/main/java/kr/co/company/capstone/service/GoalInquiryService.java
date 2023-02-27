package kr.co.company.capstone.service;

import java.util.List;

import kr.co.company.capstone.dto.goal.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GoalInquiryService {
    @GET("/goal/today")
    Call<GoalInfoListResponse<TodayGoalInfoResponse>> todayGoals();

    @GET("/goal/ongoing")
    Call<GoalInfoListResponse<SimpleGoalInfoResponse>> ongoingGoals();

    @GET("/goal/{goalId}")
    Call<GoalDetailResponse> goalDetail(@Path("goalId") long goalId);

    @GET("/goal/{goalId}/period")
    Call<GoalPeriodResponse> goalGoalCalendar(@Path("goalId") long goalId);

    @GET("/goal/view/{goalId}")
    Call<GoalDetailViewResponse> goalDetailView(@Path("goalId") long goalId);

    static GoalInquiryService getService(){
        return RetrofitBuilder.getRetrofit().create(GoalInquiryService.class);
    }
}
