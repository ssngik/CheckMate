package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.TeamMateCalendarResponse;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyRequest;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyResponse;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface TeamMateService {
    @PATCH("mate")
    Call<TeamMateInviteReplyResponse> inviteReply(@Body TeamMateInviteReplyRequest request);

    @POST("/mate")
    Call<Void> invite(@Body TeamMateInviteRequest request);

    @GET("/mate/{teamMateId}/progress")
    Call<Double> getProgressPercent(@Path("teamMateId") long teamMateId);

    @GET("/mate/{teamMateId}/calendar")
    Call<TeamMateCalendarResponse> getTeamMateGoalCalendar(@Path("teamMateId") long teamMateId);

    static TeamMateService getService(){
        return RetrofitBuilder.getRetrofit().create(TeamMateService.class);
    }
}
