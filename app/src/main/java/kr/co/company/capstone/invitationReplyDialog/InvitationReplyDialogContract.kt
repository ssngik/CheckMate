package kr.co.company.capstone.invitationReplyDialog

interface InvitationReplyDialogContract {
    interface InvitationReplyView{
        fun onInvitationAccepted(goalId : Long)
        fun onInvitationRejected()
        fun onInvitationFailed(title: String, content: String)
    }
    interface InvitationReplyPresenter{
        fun detachView()
        fun onAcceptInvitation(notificationId : Long)
        fun onRejectInvitation(notificationId : Long)
    }
    interface InvitationReplyModel{
        fun callInvitationAcceptApi(
            notificationId : Long,
            onSuccess : (Long) -> Unit,
            onFailure : (title : String, content : String) -> Unit
        )
        fun callInvitationRejectApi(
            notificationId : Long,
            onSuccess : () -> Unit,
            onFailure : (title : String, content : String) -> Unit
        )
    }
}