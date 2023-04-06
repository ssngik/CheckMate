package kr.co.company.capstone.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;

// 새 목표 생성
@Data
@AllArgsConstructor

public class GoalCreateRequest {
    private String category;
    private String title;
    private String startDate;
    private String endDate;
    private String checkDays;
    private String appointmentTime;

}