package kr.co.company.capstone.history.model

import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.dto.goal.GoalMatesResponse
import kr.co.company.capstone.dto.history.HistoryGoalInfo

interface HistoryRepository {
    fun getHistoryGoals(callback: (Result<List<HistoryGoalInfo>>) -> Unit)
    fun getGoalDetailMate(goalId: Long, callback: (Result<GoalMatesResponse>) -> Unit)
}

