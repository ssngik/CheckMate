package kr.co.company.capstone.service

import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.dto.goal.GoalInfoListResponse
import kr.co.company.capstone.dto.goal.GoalMatesResponse
import kr.co.company.capstone.dto.goal.MakeGoalRequest
import kr.co.company.capstone.dto.goal.MakeGoalResponse
import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse
import kr.co.company.capstone.dto.history.HistoryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GoalInquiryService {

    // 오늘 인증해야 할 목표 정보 조회
    @GET("/goals/today")
    fun todayGoals(): Call<GoalInfoListResponse<TodayGoalInfoResponse>>

    // 진행 중인 목표 정보 조회
    @GET("/goals/ongoing")
    fun ongoingGoals(): Call<GoalInfoListResponse<OngoingGoalInfoResponse>>

    @POST("/goals")
    fun saveGoal(@Body request: MakeGoalRequest): Call<MakeGoalResponse>

    // 조회를 요청한 유저 특화 목표 상세 정보 조회
    @GET("/goals/{goalId}/detail")
    fun userGoalDetail(@Path("goalId") goalId: Long): Call<GoalDetail>

    // 목표 상세 정보 조회 - Mate Response
    @GET("/goals/{goalId}")
    fun goalDetailMate(@Path("goalId") goalId: Long) : Call<GoalMatesResponse>

    // 성공한 목표 목록 조회
    @GET("/goals/history")
    fun userHistory(): Call<HistoryResponse>

    companion object {
        fun getService(): GoalInquiryService {
            return RetrofitBuilder.retrofit.create(GoalInquiryService::class.java)
        }
    }
}
