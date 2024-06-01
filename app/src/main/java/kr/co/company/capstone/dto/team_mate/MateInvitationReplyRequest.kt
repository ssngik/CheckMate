package kr.co.company.capstone.dto.team_mate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 팀원 초대에 대한 응답
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamMateInviteReplyRequest {
    private long notificationId; // 알림 ID
}
