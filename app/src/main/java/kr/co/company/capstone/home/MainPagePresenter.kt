package kr.co.company.capstone.home

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import kr.co.company.capstone.R
import kr.co.company.capstone.util.NavigationUtil

class MainPagePresenter(private val model: MainPageContract.Model) : MainPageContract.Presenter{

    private var mainView : MainPageContract.MainView? = null

    override fun attachView(view : MainPageContract.MainView) {
        mainView = view
    }
    override fun detachView() { mainView = null }

    override fun onViewCreated() {
        mainView?.showProgress()
        mainView?.initView()
        mainView?.setUpBtnClickListener()
        loadTodayGoals()
        loadOngoingGoals()
    }

    override fun onSetNewGoalButtonClick() {
        mainView?.navigateToNewGoalPage()
    }

    override fun loadTodayGoals() {
        model.loadTodayGoals(
            onSuccess = { result ->
                mainView?.fetchTodayGoals(result)
                mainView?.hideProgress()},

            onFailure = { error -> handleError(error)}
        )}

    override fun loadOngoingGoals() {
        model.loadOngoingGoals(
            onSuccess = { result ->
                mainView?.fetchOngoingGoals(result)
                mainView?.hideProgress()},
            onFailure = { error -> handleError(error) }
        )}

    private fun handleError(errorMessage : String){
        mainView?.showError(errorMessage)
        mainView?.hideProgress()
    }
}