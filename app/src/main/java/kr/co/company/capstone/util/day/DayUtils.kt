package kr.co.company.capstone.util.day

object DayUtils {
    fun sortCheckDays(days: String): String {
        val order = mapOf(
            '일' to 1, '월' to 2, '화' to 3,
            '수' to 4, '목' to 5, '금' to 6, '토' to 7
        )
        return days.toCharArray()
            .sortedBy { order[it] }
            .joinToString(", ")
    }
}