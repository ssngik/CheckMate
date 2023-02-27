package kr.co.company.capstone.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoalCreateRequest {
    private String category;
    private String title;
    private String goalMethod;
    private String startDate;
    private String endDate;
    private String appointmentTime;
    private String weekDays;
    private Integer minimumLike;
}
