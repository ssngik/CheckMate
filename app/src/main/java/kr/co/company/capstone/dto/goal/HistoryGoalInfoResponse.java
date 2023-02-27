package kr.co.company.capstone.dto.goal;

import lombok.Data;
import java.util.List;

@Data
public class HistoryGoalInfoResponse {
    private List<String> teamMateNames;
    private long id;
    private String category;
    private String title;
    private String  goalMethod;
    private double achievementRate;
    private String startDate;
    private String endDate;
    private String appointmentTime;
    private String weekDays;
    private Integer minimumLike;
}
