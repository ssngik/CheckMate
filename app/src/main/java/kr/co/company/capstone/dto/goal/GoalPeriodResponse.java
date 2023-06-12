package kr.co.company.capstone.dto.goal;

import lombok.Data;

@Data
public class GoalPeriodResponse {
    private String startDate; // 시작일
    private String endDate;   // 종료일
    private String schedule;  // 인증일(1 이면 인증요일, 0 이면 해당 없음)
}
