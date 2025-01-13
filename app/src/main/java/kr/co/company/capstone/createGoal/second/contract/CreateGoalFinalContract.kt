package kr.co.company.capstone.createGoal.second.contract

import kr.co.company.capstone.dto.goal.MakeGoalRequest

interface CreateGoalFinalContract {
    interface View {
        fun setButtonState(isEnabled: Boolean)
        fun navigateToGoalCreationComplete(goalRequest : MakeGoalRequest, goalId: Long)
        fun showError(errorMessage : String)
    }
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onWeekdayChecked(day: String, isChecked: Boolean)
        fun onTimeSelected(hour: Int, minute: Int)
        fun onCreateGoalButtonClicked(
            title: String,
            category: String,
            startDate: String,
            endDate: String
        )
    }
}