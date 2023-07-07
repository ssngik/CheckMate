package kr.co.company.capstone.dto.goal;

import java.io.Serializable;
import java.util.List;

import kr.co.company.capstone.dto.team_mate.TeamMatesResponse;
import lombok.Data;

@Data
public class GoalDetailResponse<TeamMatesResponse> implements Serializable {
    private long goalId; // 목표 ID
    private String category; // 카테고리
    private String title; // 목표 이름
    private String startDate;
    private String endDate;
    private String weekDays;
    private String appointmentTime;
    private String status;
    private List<TeamMatesResponse> mates; // 목표에 속한 팀원들
    private boolean inviteable; // 초대할 수 있는 목표인지
    private Uploadable uploadable; // 인증 가능 조건
    private String goalSchedule; // 목표 수행 일정
    private String mateSchedule; // 팀원의 목표 수행 일정
    private double progress; // 팀원의 목표 수행 진행률
}
