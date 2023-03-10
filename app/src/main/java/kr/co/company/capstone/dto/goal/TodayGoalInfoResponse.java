package kr.co.company.capstone.dto.goal;

import lombok.Data;

@Data
public class TodayGoalInfoResponse extends GoalInfo {
    private long id;
    private String category;
    private String title;
    private String goalMethod;
    private String weekDays;
    private boolean checked;
}
