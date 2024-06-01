package kr.co.company.capstone.dto.team_mate;

import lombok.Data;

// 팀원 초대에 대한 수락
@Data
public class TeamMateAcceptInviteResponse {
    private Long goalId; // 목표 ID
    private Long mateId; // 팀원 ID
}
