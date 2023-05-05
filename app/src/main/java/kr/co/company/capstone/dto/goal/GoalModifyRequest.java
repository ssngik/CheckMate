package kr.co.company.capstone.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoalModifyRequest {
    private String endDate; // 연장된 목표의 종료일
    private boolean timeReset; // 인증 시간 삭제 여부
    private String appointmentTime; // 변경할 인증 시간

}
