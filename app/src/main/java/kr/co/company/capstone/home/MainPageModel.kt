package kr.co.company.capstone.home

import android.util.Log
import kotlinx.coroutines.flow.callbackFlow
import kr.co.company.capstone.dto.goal.GoalInfoListResponse
import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse
import kr.co.company.capstone.service.GoalInquiryService
import retrofit2.*

class MainPageModel : MainPageContract.Model{
    private val goalService = GoalInquiryService.getService()

    override fun loadTodayGoals(
        onSuccess: (List<TodayGoalInfoResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        goalService.todayGoals().enqueue(object : Callback<GoalInfoListResponse<TodayGoalInfoResponse>> {
            override fun onResponse(
                call: Call<GoalInfoListResponse<TodayGoalInfoResponse>>,
                response: Response<GoalInfoListResponse<TodayGoalInfoResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.goals?.let{onSuccess(it)}
                } else {
                    onFailure("문제가 발생 했습니다.")
                }
            }
            override fun onFailure(call: Call<GoalInfoListResponse<TodayGoalInfoResponse>>, t: Throwable) {
                onFailure(t.message.toString())
            }
        })
    }

    override fun loadOngoingGoals(
        onSuccess: (List<OngoingGoalInfoResponse>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        goalService.ongoingGoals().enqueue(object : Callback<GoalInfoListResponse<OngoingGoalInfoResponse>>{
            override fun onResponse(
                call: Call<GoalInfoListResponse<OngoingGoalInfoResponse>>,
                response: Response<GoalInfoListResponse<OngoingGoalInfoResponse>>
            ) {
                if (response.isSuccessful){
                    response.body()?.goals?.let{onSuccess(it)}
                }else{
                    onFailure("문제가 발생 했습니다.")
                }
            }

            override fun onFailure(
                call: Call<GoalInfoListResponse<OngoingGoalInfoResponse>>,
                t: Throwable
            ) {
                onFailure(t.message.toString())
            }

        })
    }

}