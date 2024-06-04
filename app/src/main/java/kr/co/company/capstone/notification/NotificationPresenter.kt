package kr.co.company.capstone.notification


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