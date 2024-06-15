package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoalInquiryService {

    // 오늘 인증해야 할 목표 정보 조회
    @GET("/goals/today")
    Call<GoalInfoListResponse<TodayGoalInfoResponse>> todayGoals();

    // 진행 중인 목표 정보 조회
    @GET("/goals/ongoing")
    Call<GoalInfoListResponse<OngoingGoalInfoResponse>> ongoingGoals();

    // 조회를 요청한 유저 특화 목표 상세 정보 조회
    @GET("/goals/{goalId}/detail")
    Call<GoalDetail> goalDetail(@Path("goalId") long goalId);

    // 목표 인증일 조회
    @GET("/goals/{goalId}/period")
    Call<GoalPeriodResponse> goalGoalCalendar(@Path("goalId") long goalId);


    // 성공한 목표 상세 정보 조회
    @GET("/goals/history")
    Call<GoalInfoListResponse<HistoryGoalInfoResponse>> userHistory();

    static GoalInquiryService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(GoalInquiryService.class);
    }
}
