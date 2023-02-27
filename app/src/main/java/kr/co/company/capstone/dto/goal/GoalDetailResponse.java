package kr.co.company.capstone.dto.goal;

import java.io.Serializable;
import java.util.List;

import kr.co.company.capstone.dto.team_mate.TeamMateResponse;
import lombok.Data;

@Data
public class GoalDetailResponse implements Serializable {
    private long id;
    private List<TeamMateResponse> teamMates;
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
