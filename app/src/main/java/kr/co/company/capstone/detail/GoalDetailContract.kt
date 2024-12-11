package kr.co.company.capstone.detail

import kr.co.company.capstone.dto.goal.GoalCalendar
import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.dto.goal.Mate
import kr.co.company.capstone.dto.goal.Uploadable

interface GoalDetailContract {
    interface DetailView{
        fun initView(result : GoalDetail)
        fun setDoButtonStatus(statusText: String, drawableId: Int)
        fun fetchCalendar(calendarList: List<GoalCalendar>)
        fun showProgress()
        fun hideProgress()
        fun showError(errorMessage : String)
    }
    interface Presenter{
        fun detachView()
        fun loadGoalDetailViewInformation(goalId : Long)
        fun fetchCalendarViewInformation(result : GoalDetail)
        fun getWeekOfMonth() : String
        fun fetchDoButtonStatus(uploadable: Uploadable)
        fun getMates(): List<Mate>
    }
    interface Model{
        fun getWeekOfMonth() : String
        fun callGoalDetailApi(
            goalId : Long,
            onSuccess : (GoalDetail) -> Unit,
            onFailure : (String) -> Unit
        )
    }
}