package kr.co.company.capstone.dto.team_mate;

import lombok.Data;

import java.io.Serializable;


@Data
public class TeamMatesResponse implements Serializable {
    private long mateId; // 팀원 ID
    private long userId; // 유저 ID
    private String nickname; // 유저 닉네임
    private boolean uploaded;  // 해당 팀원의 목표 인증 여부
}
