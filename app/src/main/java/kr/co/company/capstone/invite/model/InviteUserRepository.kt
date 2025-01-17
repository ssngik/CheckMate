package kr.co.company.capstone.invite.model

import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest

interface InviteUserRepository {
    fun invite(
        goalId: Long,
        request: TeamMateInviteRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}