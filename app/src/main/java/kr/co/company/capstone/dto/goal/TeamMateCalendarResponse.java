package kr.co.company.capstone.dto.goal;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// 목표 캘린더 조회

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamMateCalendarResponse {
    private String startDate; // 목표 수행 시작일
    private String goalPeriod; // 목표 수행 종료일
    private String goalSchedule;  // 목표 수행 일정  ( 목표 수행일 해당 여부 )
    private String mateSchedule; // 팀원의 목표 수행 일정  ( 목표를 수행했는지에 대한 여부 )
}
