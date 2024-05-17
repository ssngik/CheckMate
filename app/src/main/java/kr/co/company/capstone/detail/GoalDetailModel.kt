package kr.co.company.capstone.detail

import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.service.GoalInquiryService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale


class GoalDetailModel : GoalDetailContract.Model {

    override fun getWeekOfMonth(): String {
        val calendar = Calendar.getInstance()
        val weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.KOREA)
        val year = calendar.get(Calendar.YEAR)
        return "${year}년 $month ${weekOfMonth}주차"
    }

    override fun callGoalDetailApi(
        goalId : Long,
        onSuccess: (GoalDetail) -> Unit,
        onFailure: (String) -> Unit
    ) {
        GoalInquiryService.getService().goalDetail(goalId).enqueue(object : Callback<GoalDetail> {
            override fun onResponse(call: Call<GoalDetail>, response: Response<GoalDetail>) {
                if (response.isSuccessful){
                    response.body()?.let { onSuccess(it) }
                }else{
                    onFailure("문제가 발생 했습니다.")
                }
            }

            override fun onFailure(call: Call<GoalDetail>, t: Throwable) {
                onFailure(t.message.toString())
            }

        })
    }

}