package kr.co.company.capstone.dto.team_mate;

import lombok.Data;

@Data
public class TeamMateInviteResponse {
    private Long inviterUserId;
    private Long goalId;
    private String inviteeNickname;
}
