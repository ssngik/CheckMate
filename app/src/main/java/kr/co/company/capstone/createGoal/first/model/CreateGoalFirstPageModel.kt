package kr.co.company.capstone.createGoal.first.model

import kr.co.company.capstone.createGoal.first.contract.CreateGoalFirstPageContract
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateGoalFirstPageModel : CreateGoalFirstPageContract.Model {
    private var title: String = ""
    private var category: String = ""
    private var startDate: String = ""
    private var endDate: String = ""

    // Field 조건에 맞는 Date 형식 Format
    override fun updateDateRange(selection: Pair<Long, Long>): Pair<String, String>  {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        startDate = simpleDateFormat.format(Date(selection.first))
        endDate = simpleDateFormat.format(Date(selection.second))

        return Pair(startDate, endDate)
    }

    override fun updateTitle(title: String) {
        this.title = title
    }

    override fun updateCategory(category: String) {
        this.category = category
    }

    // 모든 필드 입력 여부
    override fun isInputValid(): Boolean {
        return title.isNotBlank() && category.isNotBlank() && startDate.isNotBlank() && endDate.isNotBlank()
    }

    override fun getTitle(): String = title
    override fun getCategory(): String = category
    override fun getStartDate(): String = startDate
    override fun getEndDate(): String = endDate

}