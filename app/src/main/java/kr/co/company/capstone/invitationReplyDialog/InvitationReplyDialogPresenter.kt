package kr.co.company.capstone.invitationReplyDialog

class InvitationReplyDialogPresenter (
    private var invitationReplyView : InvitationReplyDialogContract.InvitationReplyView?,
    private val model : InvitationReplyDialogContract.InvitationReplyModel) : InvitationReplyDialogContract.InvitationReplyPresenter {
    override fun onAcceptInvitation(notificationId : Long) {
        model.callInvitationAcceptApi(
            notificationId,
            onSuccess = { goalId -> invitationReplyView?.onInvitationAccepted(goalId)},
            onFailure = { title, content -> invitationReplyView?.onInvitationFailed(title, content)}
        )
    }

    override fun onRejectInvitation(notificationId : Long) {
        model.callInvitationRejectApi(
            notificationId,
            onSuccess = { invitationReplyView?.onInvitationRejected()},
            onFailure = { title, content -> invitationReplyView?.onInvitationFailed(title, content)}
        )
    }

    override fun detachView() {
        invitationReplyView = null
    }

}