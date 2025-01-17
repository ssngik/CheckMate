package kr.co.company.capstone.createGoal.first.presenter

import kr.co.company.capstone.createGoal.first.contract.CreateGoalFirstPageContract

class CreateGoalFirstPagePresenter(
    private val model : CreateGoalFirstPageContract.Model
) : CreateGoalFirstPageContract.Presenter{

    private var view: CreateGoalFirstPageContract.View? = null

    // 목표 이름 입력시 모든 필드 선택 여부 검증
    override fun onTitleChanged(title: String) {
        model.updateTitle(title)
        updateButtonState()
    }

    // 카테고리 선택 후 모든 필드 선택 여부 검증
    override fun onCategorySelected(category: String) {
        model.updateCategory(category)
        updateButtonState()
    }

    // 기간 선택 후 모든 필드 선택 여부 검증
    override fun onDateSelected(selection: Pair<Long, Long>) {
        val formattedDate = model.updateDateRange(selection)
        view?.setSelectedDate(formattedDate) // UI 업데이트
        updateButtonState()
    }

    // 다음 버튼 클릭시
    override fun onNextButtonClicked() {
        if (model.isInputValid()) {
            view?.navigateToSecondPage(
                model.getStartDate(),
                model.getEndDate(),
                model.getCategory(),
                model.getTitle()
            )
        }
    }

    // 필드 입력 여부에 따른 버튼 활성화 변경
    private fun updateButtonState() {
        view?.setButtonEnabled(model.isInputValid())
    }
    override fun attachView(view: CreateGoalFirstPageContract.View) { this.view = view }

    override fun detachView() { view = null }
}