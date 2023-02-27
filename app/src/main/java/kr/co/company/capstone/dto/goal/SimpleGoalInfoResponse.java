package kr.co.company.capstone.dto.goal;

import lombok.Data;

@Data
public class SimpleGoalInfoResponse extends GoalInfo{
    private long id;
    private String category;
    private String title;
    private String goalMethod;
    private String weekDays;

    @Override
    public boolean isChecked() {
        return false;
    }
}
