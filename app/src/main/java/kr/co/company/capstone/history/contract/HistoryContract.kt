package kr.co.company.capstone.history.contract

import kr.co.company.capstone.dto.history.HistoryGoalInfo
interface HistoryContract {

    interface View {
        fun showHistoryGoals(goals: List<HistoryGoalInfo>)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadHistoryGoals()
    }
}