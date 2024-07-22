package kr.co.company.capstone.createGoal.first

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateGoalFirstPageModel : CreateGoalFirstPageContract.Model {
    override var startDate: String = ""
    override var endDate: String = ""

    // Field 조건에 맞는 Date 형식 Format
    override fun formatDate(selection: Pair<Long, Long>) : Pair<String, String>{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        startDate = simpleDateFormat.format(Date(selection.first))
        endDate = simpleDateFormat.format(Date(selection.second))

        return Pair(startDate, endDate)
    }

}