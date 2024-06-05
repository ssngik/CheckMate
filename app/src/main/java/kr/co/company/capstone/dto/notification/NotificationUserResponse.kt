package kr.co.company.capstone.dto.notification

data class NotificationUserResponse(
    val notificationId: Long,
    val title: String,
    val content: String,
    val checked: Boolean,
    val sendAt: String,
    val type: String
    )
