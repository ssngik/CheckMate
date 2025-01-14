package kr.co.company.capstone.createGoal.second.model

import kr.co.company.capstone.dto.goal.MakeGoalRequest

interface GoalCreateRepository {
    fun saveGoal(
        request: MakeGoalRequest,
        onSuccess: (goalId: Long) -> Unit,
        onError: (String) -> Unit
    )
}