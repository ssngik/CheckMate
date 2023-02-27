package kr.co.company.capstone.dto.goal;

import java.io.Serializable;

import lombok.Data;

@Data
public class GoalCreateResponse implements Serializable {
    private long id;
    private String category;
    private String title;
    private String goalMethod;
    private String startDate;
    private String endDate;
    private String appointmentTime;
    private String weekDays;
    private String goalStatus;
    private Integer minimumLike;
}
