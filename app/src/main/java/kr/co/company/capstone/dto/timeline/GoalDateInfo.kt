package kr.co.company.capstone.dto.timeline

data class GoalDateInfo(
    val startDate: String, // 목표 시작 날짜
    val endDate: String,
    val weekDays: List<String>  // 목표 요일
)
