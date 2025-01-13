package kr.co.company.capstone.createGoal.second.model

import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.goal.MakeGoalRequest
import kr.co.company.capstone.dto.goal.MakeGoalResponse
import kr.co.company.capstone.service.GoalInquiryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoalCreateRepositoryImpl(
    private val goalService : GoalInquiryService
) : GoalCreateRepository {
    override fun saveGoal(
        request: MakeGoalRequest,
        onSuccess: (goalId: Long) -> Unit,
        onError: (String) -> Unit
    ) {
        goalService.saveGoal(request).enqueue(object : Callback<MakeGoalResponse> {
            override fun onResponse(call: Call<MakeGoalResponse>, response: Response<MakeGoalResponse>) {
                if (response.isSuccessful) {
                    val goalId= response.body()?.goalId ?: 0L
                    onSuccess(goalId)
                } else {
                    val em = ErrorMessage.getErrorByResponse(response)
                    val errorMessage = when (em.code) {
                        "GOAL-002" -> "목표 개수를 초과했어요."
                        "GOAL-003" -> "목표 기간을 확인해 보세요."
                        "GOAL-004" -> "인증 요일을 확인해 보세요."
                        else ->  "목표를 생성할 수 없어요."
                    }
                    onError(errorMessage)
                }
            }
            override fun onFailure(call: Call<MakeGoalResponse>, t: Throwable) {
                onError(t.message ?: "Unknown error")
            }
        })
    }
}