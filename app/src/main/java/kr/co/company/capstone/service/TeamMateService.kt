package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.GoalCalendar;
import kr.co.company.capstone.dto.team_mate.TeamMateAcceptInviteResponse;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyRequest;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface TeamMateService {

    // 초대 수락
    @PATCH("/mates/accept")
    Call<TeamMateAcceptInviteResponse> inviteReply(@Body TeamMateInviteReplyRequest request);

    @PATCH("/mates/reject")
    Call<Void> inviteReject(@Body TeamMateInviteReplyRequest request);

    // 팀원 초대 요청
    @POST("/goals/{goalId}/mates")
    Call<Void> invite(@Path("goalId") Long goalId, @Body TeamMateInviteRequest request);


    static TeamMateService getService(){
        return RetrofitBuilder.getRetrofit().create(TeamMateService.class);
    }
}
