package kr.co.company.capstone.dto.goal;

import java.io.Serializable;
import java.util.List;

import kr.co.company.capstone.dto.team_mate.TeamMatesResponse;
import lombok.Data;

@Data
public class GoalDetailResponse<TeamMatesResponse> implements Serializable {
    private long goalId;
    private List<TeamMatesResponse> mates;
    private String category;
    private String title;
    private String goalMethod;
    private String startDate;
    private String endDate;
    private String weekDays;
    private String appointmentTime;
    private String goalStatus;
    private boolean inviteable;
    private Integer minimumLike;
    private Uploadable uploadable;
}
