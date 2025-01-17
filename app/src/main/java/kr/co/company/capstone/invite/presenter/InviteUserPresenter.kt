package kr.co.company.capstone.invite.presenter

import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest
import kr.co.company.capstone.invite.contract.InviteUserContract
import kr.co.company.capstone.invite.model.InviteUserRepository
class InviteUserPresenter(
    private val repository: InviteUserRepository
) : InviteUserContract.Presenter {
    private var view: InviteUserContract.View? = null

    override fun attachView(view: InviteUserContract.View) { this.view = view }
    override fun detachView() { this.view = null }
    override fun onInviteButtonClicked(goalId: Long, request: TeamMateInviteRequest) {
        repository.invite(
            goalId = goalId,
            request = request,
            onSuccess = {
                view?.showInvitationResultDialog(
                    title = "초대 성공",
                    body = "초대 요청을 보냈어요!\n응답이 오면 알려드릴게요",
                    emoji = true,
                    positiveButtonText = "더 초대하기",
                    onPositiveAction = { /* dismiss */ },
                    negativeButtonText = "메인 화면으로",
                    onNegativeAction = { view?.navigateToHome() }
                )
            },
            onError = { error ->
                val errorMessage = parseErrorMessage(error)
                view?.showInvitationResultDialog(
                    title = "초대 실패",
                    body = errorMessage,
                    emoji = false,
                    positiveButtonText = "다시 초대하기",
                    onPositiveAction = { /* dismiss */ },
                    negativeButtonText = "다음에 초대하기",
                    onNegativeAction = { view?.navigateToPreviousScreen() }
                )
            }
        )
    }

    private fun parseErrorMessage(error: String): String {
        return when (error) {
            "USER-001" -> "존재하지 않는 유저입니다."
            "MATE-002" -> "이미 해당 목표를 진행 중인 유저입니다."
            "MATE-003" -> "이미 초대를 보낸 유저입니다."
            "onFailure" -> "통신 오류가 발생했습니다. 다시 시도해주세요."
            else -> "알 수 없는 오류가 발생했습니다."
        }
    }
}