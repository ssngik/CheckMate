package kr.co.company.capstone.createGoal.first.contract

interface CreateGoalFirstPageContract {
    interface View{
        fun setSelectedDate(selection: Pair<String, String>)
        fun setButtonEnabled(isEnabled: Boolean)
        fun navigateToSecondPage(startDate : String, endDate : String, category : String, title : String)
    }
    interface Presenter {
        fun onTitleChanged(title: String)
        fun onCategorySelected(category: String)
        fun onDateSelected(selection : Pair<Long, Long>)
        fun onNextButtonClicked()
        fun attachView(view: View)
        fun detachView()
    }
    interface Model{
        fun updateTitle(title: String)
        fun updateCategory(category: String)
        fun isInputValid() : Boolean
        fun updateDateRange(selection: Pair<Long, Long>) : Pair<String, String>
        fun getTitle(): String
        fun getCategory(): String
        fun getStartDate(): String
        fun getEndDate(): String
    }

}