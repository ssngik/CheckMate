package kr.co.company.capstone.createGoal.first.contract

interface CreateGoalFirstPageContract {
    interface View{
        fun getEnteredTitle() : String
        fun getSelectedDate() : String
        fun getSelectedCategory() : String
        fun setSelectedDate(selection: Pair<String, String>)
        fun setButtonDrawable(drawableRes: Int, isEnabled: Boolean)
        fun navigateToSecondPage(startDate : String, endDate : String, category : String, title : String)
    }
    interface Presenter{
        fun onTitleTextChanged()
        fun onCategorySelected()
        fun onDateSelected(selection : Pair<Long, Long>)
        fun fieldsAreSet() : Boolean
        fun onNextButtonClicked()
    }
    interface Model{
        var startDate : String
        var endDate : String
        fun formatDate(selection: Pair<Long, Long>) : Pair<String, String>
    }

}