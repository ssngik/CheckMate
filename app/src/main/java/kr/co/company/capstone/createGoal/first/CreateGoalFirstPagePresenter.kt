package kr.co.company.capstone.createGoal.first

import kr.co.company.capstone.R

class CreateGoalFirstPagePresenter(
    private val view : CreateGoalFirstPageContract.View,
    private val model : CreateGoalFirstPageContract.Model
) :CreateGoalFirstPageContract.Presenter{

    // 목표 이름 입력시 모든 필드 선택 여부 검증
    override fun onTitleTextChanged() {
        setButtonEnabledIfAllSet()
    }

    // 카테고리 선택 후 모든 필드 선택 여부 검증
    override fun onCategorySelected() {
        setButtonEnabledIfAllSet()
    }

    // 기간 선택 후 모든 필드 선택 여부 검증
    override fun onDateSelected(selection: Pair<Long, Long>) {
        view.setSelectedDate(model.formatDate(selection))
        setButtonEnabledIfAllSet()
    }

    // 모든 필드 선택 완료 여부
    override fun fieldsAreSet() : Boolean {
        return view.getEnteredTitle().isNotEmpty() &&
                view.getSelectedCategory().isNotEmpty() &&
                view.getSelectedDate().isNotEmpty()
    }

    // 다음 버튼 클릭시
    override fun onNextButtonClicked() {
        if (fieldsAreSet()){
            view.navigateToSecondPage(
                model.startDate,
                model.endDate,
                view.getSelectedCategory(),
                view.getEnteredTitle()
            )
        }
    }


    // 필드 입력에 따른 버튼 활성화 변경
    private fun setButtonEnabledIfAllSet() {
        val drawable = if (fieldsAreSet()){
            R.drawable.btn_main
        } else{
            R.drawable.btn_main_disabled
        }

        view.setButtonDrawable(drawable, fieldsAreSet())
    }

}