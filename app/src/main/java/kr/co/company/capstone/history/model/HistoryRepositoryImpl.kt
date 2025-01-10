package kr.co.company.capstone.history.model

import kr.co.company.capstone.dto.goal.GoalMatesResponse
import kr.co.company.capstone.dto.history.HistoryGoalInfo
import kr.co.company.capstone.dto.history.HistoryResponse
import kr.co.company.capstone.service.GoalInquiryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryRepositoryImpl(
    private val goalInquiryService: GoalInquiryService,
) : HistoryRepository {

    override fun getHistoryGoals(callback: (Result<List<HistoryGoalInfo>>) -> Unit) {
        goalInquiryService.userHistory().enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (response.isSuccessful) {
                    val goals = response.body()?.goals ?: emptyList()
                    callback(Result.success(goals))
                } else {
                    callback(Result.failure(Exception("Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun getGoalDetailMate(goalId: Long, callback: (Result<GoalMatesResponse>) -> Unit) {
        goalInquiryService.goalDetailMate(goalId).enqueue(object : Callback<GoalMatesResponse> {
            override fun onResponse(call: Call<GoalMatesResponse>, response: Response<GoalMatesResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it))
                    }
                } else {
                    callback(Result.failure(Exception("Error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<GoalMatesResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

}