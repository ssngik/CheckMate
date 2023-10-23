package kr.co.company.capstone.dto.goal;

import lombok.Data;

@Data
public class TodayGoalInfoResponse extends GoalInfo {
    private long goalId;
    private String category;
    private String title;
    private String checkDays;
    private boolean checked;
}
