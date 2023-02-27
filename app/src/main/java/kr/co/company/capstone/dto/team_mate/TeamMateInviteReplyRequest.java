package kr.co.company.capstone.dto.team_mate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TeamMateInviteReplyRequest {
    private long notificationId;
    private boolean accept;
}
