package kr.co.company.capstone.history.presenter

import kr.co.company.capstone.history.contract.HistoryContract
import kr.co.company.capstone.history.model.HistoryRepository
import kr.co.company.capstone.history.model.HistoryRepositoryImpl
import kr.co.company.capstone.service.GoalInquiryService

class HistoryPresenter(
    private val view: HistoryContract.View,
) : HistoryContract.Presenter {

    private val repository: HistoryRepository by lazy {
        HistoryRepositoryImpl(GoalInquiryService.getService())
    }

    // 히스토리 조회
    override fun loadHistoryGoals() {
        repository.getHistoryGoals { result ->
            result.onSuccess { goals ->
                view.showHistoryGoals(goals)
            }.onFailure { error ->
                view.showError(error.message ?: "히스토리 로딩 문제가 발생했어요.")
            }
        }
    }

    // userId
    fun getUserIdForGoal(goalId: Long, nicknameProvider: () -> String, callback: (Long?) -> Unit) {
        repository.getGoalDetailMate(goalId) { result ->
            result.onSuccess { detail ->
                val nickname = nicknameProvider()
                val userId = detail.mates.find { it.nickname == nickname }?.userId
                callback(userId)
            }.onFailure {
                view.showError("목표 정보를 가져오지 못했어요.")
                callback(null)
            }
        }
    }
}