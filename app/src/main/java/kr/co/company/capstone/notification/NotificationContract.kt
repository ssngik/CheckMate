package kr.co.company.capstone.notification

import kr.co.company.capstone.dto.notification.NotificationResponse

class NotificationContract {
    interface NotificationView{
        fun fetchNotificationsRecyclerView(result : NotificationResponse)
        fun appendNotifications(result: NotificationResponse)
        fun showErrorDialog(errorMessage : String)
    }
    interface NotificationPresenter{
        fun loadNotifications()
        fun loadAdditionalNotifications(cursorId : Long)
        fun detachView()
    }
    interface NotificationModel{
        // 최초 알림 호출 (10개)
        fun loadNotifications(
            onSuccess : (NotificationResponse) -> Unit,
            onFailure: (String) -> Unit
        )

        // hasNext인 경우 추가 알림 호출
        fun loadAdditionalNotifications(
            cursorId : Long,
            size : Int,
            onSuccess : (NotificationResponse) -> Unit,
            onFailure: (String) -> Unit
        )

    }
}