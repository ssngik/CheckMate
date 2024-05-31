package kr.co.company.capstone.dto.notification


data class NotificationDetailResponse(
    val title : String,
    val content : String,
    val type : String,
    val attributes : String
)