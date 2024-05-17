package kr.co.company.capstone.dto.goal

import java.time.LocalDate

data class GoalCalendar (
    val date : LocalDate,
    val isGoalDay : Boolean,
)
