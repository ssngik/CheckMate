package kr.co.company.capstone.dto.notification


data class NotificationResponse(
    val notifications: List<NotificationUserResponse>,
    val hasNext: Boolean
)
