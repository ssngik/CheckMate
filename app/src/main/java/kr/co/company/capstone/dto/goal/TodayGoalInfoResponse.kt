package kr.co.company.capstone.dto.goal

data class TodayGoalInfoResponse(
    val goalId : Long,
    val category : String,
    val title : String,
    val checkDays : String,
    val checked : Boolean
)