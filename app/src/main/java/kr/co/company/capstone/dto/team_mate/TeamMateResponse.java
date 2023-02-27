package kr.co.company.capstone.dto.team_mate;

import lombok.Data;

@Data
public class TeamMateResponse {
    private long id;
    private long userId;
    private String nickname;
    private boolean uploaded;
}
