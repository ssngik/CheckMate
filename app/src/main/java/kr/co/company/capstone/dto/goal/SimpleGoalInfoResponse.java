package kr.co.company.capstone.dto.goal;

import lombok.Data;

@Data
public class SimpleGoalInfoResponse extends OngoingGoalInfo {
    private long goalId;
    private String title;
    private String category;
    private String weekDays;
}
