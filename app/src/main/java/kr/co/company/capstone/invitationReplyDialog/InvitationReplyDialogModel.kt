package kr.co.company.capstone.invitationReplyDialog

import kr.co.company.capstone.dto.team_mate.MateInvitationAcceptResponse
import kr.co.company.capstone.dto.team_mate.MateInvitationReplyRequest
import kr.co.company.capstone.service.TeamMateService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitationReplyDialogModel : InvitationReplyDialogContract.InvitationReplyModel {
    override fun callInvitationAcceptApi(
        notificationId : Long,
        onSuccess : (goalId : Long) -> Unit,
        onFailure : (title : String, content : String) -> Unit
    ) {
        TeamMateService.service().invitationAccept(MateInvitationReplyRequest(notificationId))
            .enqueue(object : Callback<MateInvitationAcceptResponse> {
                override fun onResponse(
                    call: Call<MateInvitationAcceptResponse>,
                    response: Response<MateInvitationAcceptResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.goalId?.let { onSuccess(it) }
                    } else {
                        onFailure("합류 실패","합류할 수 있는 기간이 지났어요.")
                    }
                }

                override fun onFailure(call: Call<MateInvitationAcceptResponse>, t: Throwable) {
                    onFailure("문제 발생","문제가 발생했습니다. 다시 시도해 주세요.")
                }
            })
    }

    override fun callInvitationRejectApi(
        notificationId : Long,
        onSuccess : () -> Unit,
        onFailure: (title : String, content : String) -> Unit
    ) {
        TeamMateService.service().invitationReject(MateInvitationReplyRequest(notificationId)).enqueue(object : Callback<Void?>{
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful){
                    onSuccess()
                }else{
                    onFailure("!", "이미 합류 기간이 지났습니다.")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                onFailure("문제 발생","문제가 발생했습니다. 다시 시도해 주세요.")
            }

        })

    }

}