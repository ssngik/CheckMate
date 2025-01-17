package kr.co.company.capstone.invite.model

import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest
import kr.co.company.capstone.service.TeamMateService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteUserRepositoryImpl(
    private val goalService: TeamMateService
): InviteUserRepository {
    override fun invite(
        goalId: Long,
        request: TeamMateInviteRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        goalService.invite(goalId, request).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = ErrorMessage.getErrorByResponse(response)
                    val errorMessage = errorBody?.code ?: "알 수 없는 오류 발생"
                    onError(errorMessage)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                onError("네트워크 오류: ${t.message}")
            }
        })
    }
}