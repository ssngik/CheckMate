package kr.co.company.capstone.invite.contract

import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest

interface InviteUserContract {
    interface View {
        fun navigateToPreviousScreen()
        fun navigateToHome()

        fun showInvitationResultDialog(
            title: String,
            body: String,
            emoji: Boolean,
            positiveButtonText: String,
            onPositiveAction: () -> Unit,
            negativeButtonText: String? = null,
            onNegativeAction: (() -> Unit)? = null
        )
    }
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onInviteButtonClicked(goalId: Long, request: TeamMateInviteRequest)
    }
}