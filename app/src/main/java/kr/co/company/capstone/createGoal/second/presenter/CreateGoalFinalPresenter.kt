package kr.co.company.capstone.createGoal.second.presenter

import kr.co.company.capstone.createGoal.second.contract.CreateGoalFinalContract
import kr.co.company.capstone.createGoal.second.model.GoalCreateRepository
import kr.co.company.capstone.dto.goal.MakeGoalRequest

class CreateGoalFinalPresenter(
    private val goalCreateRepository: GoalCreateRepository
) : CreateGoalFinalContract.Presenter {
    private var view: CreateGoalFinalContract.View? = null

    // 선택된 요일 목록
    private val selectedWeekDays = mutableListOf<String>()
    // 사용자 선택 인증 시간("HH:mm:ss")
    private var appointmentTime: String = ""

    override fun attachView(view: CreateGoalFinalContract.View) { this.view = view }
    override fun detachView() { this.view = null }

    override fun onWeekdayChecked(day: String, isChecked: Boolean) {
        if (isChecked) {
            if (!selectedWeekDays.contains(day)) {
                selectedWeekDays.add(day)
            }
        } else {
            selectedWeekDays.remove(day)
        }
        checkEssentialInput()
    }

    override fun onTimeSelected(selectedAmPm: Int, hour: Int, minute: Int) {
        appointmentTime = String.format("%02d:%02d:00", hour, minute) // api 요청 형식
        checkEssentialInput()
    }

    override fun onCreateGoalButtonClicked(
        title: String,
        category: String,
        startDate: String,
        endDate: String,
    ) {
        val weekDays = selectedWeekDays.joinToString("")
        val makeGoalRequest = MakeGoalRequest(
            title = title,
            category = category,
            startDate = startDate,
            endDate = endDate,
            checkDays = weekDays,
            appointmentTime = appointmentTime
        )

        // 목표 생성 호출
        goalCreateRepository.saveGoal(
            request = makeGoalRequest,
            onSuccess = { goalId ->
                view?.navigateToGoalCreationComplete(makeGoalRequest, goalId)
            },
            onError = { errorMsg ->
                view?.showError(errorMsg)
            }
        )
    }

    // 필수 필드 선택 여부 (현재는 요일)
    private fun checkEssentialInput() {
        val isDaySelected = selectedWeekDays.isNotEmpty()
        if (isDaySelected) {
            view?.setButtonState(true)
        } else {
            view?.setButtonState(false)
        }
    }
}