package kr.co.company.capstone.dto.history

data class HistoryGoalInfo(
    val goalId: Long,
    val category: String,                  // 목표 카테고리
    val title: String,                    // 목표 이름
    val startDate: String,                // 시작 날짜
    val endDate: String,                  // 종료 날짜
    val appointmentTime: String?,         // 인증 시간 (nullable)
    val checkDays: String,                // 인증 요일
    val achievementPercent: Float,        // 성취율
    val mateNicknames: List<String>       // 팀원 닉네임
)