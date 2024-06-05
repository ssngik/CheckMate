package kr.co.company.capstone.notification

import android.util.Log


class NotificationPresenter(
    private var notificationView : NotificationContract.NotificationView?,
    private val model : NotificationContract.NotificationModel) : NotificationContract.NotificationPresenter {
    private var cursorId : Long? = null
    private var hasNext : Boolean = true

    // 최초 알림 호출
    override fun loadNotifications() {
        model.loadNotifications(
            onSuccess = { result ->
                hasNext = result.hasNext
                cursorId = result.notifications.lastOrNull()?.notificationId
                notificationView?.fetchNotificationsRecyclerView(result) },
            onFailure = {error -> notificationView?.showErrorDialog(error) })
    }

    // 추가적인 알림 호출
    override fun loadAdditionalNotifications(cursorId : Long) {
        if (!hasNext) return // 더 불러올 알림이 없는 경우

        model.loadAdditionalNotifications(
            cursorId = cursorId,
            size = 10,
            onSuccess = { result ->
                this.cursorId = result.notifications.lastOrNull()?.notificationId
                hasNext = result.hasNext
                notificationView?.appendNotifications(result) },
            onFailure = { error -> notificationView?.showErrorDialog(error)}
        )
    }

    override fun detachView() {
        notificationView = null
    }
}