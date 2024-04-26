package kr.co.company.capstone.home

import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse

interface MainPageContract {
    interface MainView{
        fun initView()
        fun showProgress()
        fun hideProgress()
        fun navigateToNewGoalPage()
        fun setUpBtnClickListener()
        fun fetchTodayGoals(result: List<TodayGoalInfoResponse>)
        fun fetchOngoingGoals(result : List<OngoingGoalInfoResponse>)
        fun showError(errorMessage : String)
    }

    interface Presenter{
        fun attachView(view : MainView)
        fun detachView()
        fun onViewCreated()
        fun onSetNewGoalButtonClick()
        fun loadTodayGoals()
        fun loadOngoingGoals()
    }

    interface Model{
        fun loadTodayGoals(
            onSuccess : (List<TodayGoalInfoResponse>) -> Unit,
            onFailure : (String) -> Unit
        )
        fun loadOngoingGoals(
            onSuccess : (List<OngoingGoalInfoResponse>) -> Unit,
            onFailure : (String) -> Unit
        )
    }
}