package kr.co.company.capstone.dto.team_mate;

import lombok.Data;

@Data
public class TeamMateInviteReplyResponse {
    private Long goalId; // 목표 ID
    private Long mateId; // 팀원 ID
}
