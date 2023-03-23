package kr.co.company.capstone.service;

import java.util.List;

import kr.co.company.capstone.dto.goal.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GoalInquiryService {

    // 오늘 인증해야 할 목표 정보 조회
    @GET("/goals/today")
    Call<GoalInfoListResponse<TodayGoalInfoResponse>> todayGoals();

    // 진행 중인 목표 정보 조회
    @GET("/goals/ongoing")
    Call<GoalInfoListResponse<SimpleGoalInfoResponse>> ongoingGoals();

    //목표 상세 정보 조회
    @GET("/goals/{goalId}")
    Call<GoalDetailResponse> goalDetail(@Path("goalId") long goalId);

    // 목표 인증일 조회
    @GET("/goals/{goalId}/period")
    Call<GoalPeriodResponse> goalGoalCalendar(@Path("goalId") long goalId);

    // 목표 상세 정보 조회
    @GET("/goals/view/{goalId}")
    Call<GoalDetailViewResponse> goalDetailView(@Path("goalId") long goalId);

    static GoalInquiryService getService(){
        return RetrofitBuilder.getRetrofit().create(GoalInquiryService.class);
    }
}
