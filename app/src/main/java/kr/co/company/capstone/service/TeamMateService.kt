package kr.co.company.capstone.service

import kr.co.company.capstone.dto.team_mate.MateInvitationAcceptResponse
import kr.co.company.capstone.dto.team_mate.MateInvitationReplyRequest
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TeamMateService {
    // 초대 수락
    @PATCH("/mates/accept")
    fun invitationAccept(@Body request: MateInvitationReplyRequest): Call<MateInvitationAcceptResponse>

    @PATCH("/mates/reject")
    fun invitationReject(@Body request: MateInvitationReplyRequest): Call<Void>

    // 팀원 초대 요청
    @POST("/goals/{goalId}/mates")
    fun invite(@Path("goalId") goalId: Long, @Body request: TeamMateInviteRequest): Call<Void>

    companion object {
        fun service(): TeamMateService{
            return RetrofitBuilder.getRetrofit().create(TeamMateService::class.java)
        }
    }
}
