package kr.co.company.capstone.dto.goal


data class OngoingGoalInfoResponse (
    val goalId : Long,
    val title : String,
    val category : String,
    val weekDays : String
)
