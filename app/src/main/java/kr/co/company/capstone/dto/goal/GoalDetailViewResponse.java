package kr.co.company.capstone.dto.goal;

import lombok.Data;

@Data
public class GoalDetailViewResponse {
    private GoalDetailResponse goalDetailResponse;
    private TeamMateCalendarResponse teamMateCalendarResponse;
    private double progress;
}
