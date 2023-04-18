package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.goal.TeamMateCalendarResponse;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyRequest;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteReplyResponse;
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest;
import retrofit2.Call;
import retrofit2.http.*;

public interface TeamMateService {

    // 팀원 초대 요청
    @POST("/goals/{goalId}/mates")
    Call<Void> invite(@Path("goalId") Long goalId, @Body TeamMateInviteRequest request);

    // 팀원의 목표 캘린더 조회
    @GET("/mates/{mateId}/calendar")
    Call<TeamMateCalendarResponse> getTeamMateGoalCalendar(@Path("teamMateId") long teamMateId);

    static TeamMateService getService(){
        return RetrofitBuilder.getRetrofit().create(TeamMateService.class);
    }
}
