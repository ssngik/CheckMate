package kr.co.company.capstone.dto.goal

data class GoalDetail(
    val goalId: Int,
    val category: String,
    val title: String,
    val startDate: String,
    val endDate: String,
    val appointmentTime: String?, // null 허용
    val weekDays: String,
    val status: String,
    val inviteable: Boolean,
    val uploadable : Uploadable,
    val goalSchedule: String,
    val mateSchedule: String,
    val achievementPercent: Double,
    val mates : List<Mate>
)
