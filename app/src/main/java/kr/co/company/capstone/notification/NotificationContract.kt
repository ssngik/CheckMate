package kr.co.company.capstone.notification

import kr.co.company.capstone.dto.notification.NotificationResponse

class NotificationContract {
    interface NotificationView{
        fun fetchNotificationsRecyclerView(result : NotificationResponse)
        fun showErrorDialog(errorMessage : String)
    }
    interface NotificationPresenter{
        fun loadNotifications()
        fun detachView()
    }
    interface NotificationModel{
        fun callLoadAllUserNotificationsApi(
            onSuccess : (NotificationResponse) -> Unit,
            onFailure: (String) -> Unit
        )

    }
}