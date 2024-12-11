package kr.co.company.capstone.detail

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kr.co.company.capstone.R
import kr.co.company.capstone.dto.goal.GoalCalendar
import kr.co.company.capstone.dto.goal.GoalDetail
import kr.co.company.capstone.dto.goal.Mate
import kr.co.company.capstone.dto.goal.Uploadable
import java.time.LocalDate

class GoalDetailPresenter(private var detailView : GoalDetailContract.DetailView?) : GoalDetailContract.Presenter{
    private val model = GoalDetailModel()
    private var mates: List<Mate> = emptyList()

    override fun detachView() {
        this.detailView = null
    }

    // 목표 상세 정보 API 호출
    override fun loadGoalDetailViewInformation(goalId : Long){
        detailView?.showProgress()
        model.callGoalDetailApi(
            goalId,
            onSuccess = {result ->
                mates = result.mates
                detailView?.initView(result)
                detailView?.hideProgress() },
            onFailure = {error -> handleError(error)}
        )
    }

    // 목표 수행 일정 RecyclerView
    override fun fetchCalendarViewInformation(result: GoalDetail) {
        val startDate = LocalDate.parse(result.startDate)
        val schedule = result.goalSchedule
        val calendarList = mutableListOf<GoalCalendar>()
        var startToEndDate = startDate // 전체 기간 날짜

        for (i in schedule){
            val isGoalDay = i.toString() == "1" // 목표 인증일 여부

            // adapter에 전달할 List
            val calendarDay = GoalCalendar(startToEndDate, isGoalDay)
            calendarList.add(calendarDay)

            // 날짜 증가
            startToEndDate = startToEndDate.plusDays(1)
        }

        detailView?.fetchCalendar(calendarList)
    }

    // 현재 날짜의 주차 정보
    override fun getWeekOfMonth(): String {
        return model.getWeekOfMonth()
    }

    // 목표 진행 여부에 따른 버튼 상태 변경
    override fun fetchDoButtonStatus(uploadable: Uploadable) {
        val isUploaded = uploadable.uploaded
        val isCheckDay = uploadable.checkDay
        val isTimeOver = uploadable.timeOver

        val buttonText = when {
            isUploaded -> "오늘 목표를 성공했어요"
            !isCheckDay -> "인증 요일이 아니에요."
            isTimeOver -> "인증 시간이 지났어요."
            else -> "" // uploadable -> true
        }

        val drawableId = when {
            isUploaded -> R.drawable.emoji_winking_face
            !isCheckDay || isTimeOver -> R.drawable.emojis_frowning_face
            else -> 0
        }

        detailView?.setDoButtonStatus(buttonText, drawableId)
    }

    override fun getMates(): List<Mate> = mates

    private fun handleError(errorMessage: String) {
        detailView?.showError(errorMessage)
        detailView?.hideProgress()
    }

}