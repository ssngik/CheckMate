package kr.co.company.capstone.dto.goal;

import lombok.Data;
import java.util.List;

@Data
public class HistoryGoalInfoResponse {
    private long goalId;            // 목표 ID
    private String category;        // 목표 카테고리
    private String title;           // 목표 이름
    private String startDate;       // 목표 시작일
    private String endDate;         // 목표 종료일
    private String checkDays;       // 목표 인증 요일
    private String appointmentTime; // 목표 인증 시간
    private double achievementRate; // 유저의 최종 성취율
    private List<String> mateNicknames; // 팀원들의 닉네임
}
