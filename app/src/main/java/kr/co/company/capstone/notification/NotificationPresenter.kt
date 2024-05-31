package kr.co.company.capstone.notification


class NotificationPresenter(
    private var notificationView : NotificationContract.NotificationView?,
    private val model : NotificationContract.NotificationModel) : NotificationContract.NotificationPresenter {

    override fun loadNotifications() {
        model.callLoadAllUserNotificationsApi(
            onSuccess = {result -> notificationView?.fetchNotificationsRecyclerView(result)},
            onFailure = {error -> notificationView?.showErrorDialog(error) })
    }

    override fun detachView() {
        notificationView = null
    }
}