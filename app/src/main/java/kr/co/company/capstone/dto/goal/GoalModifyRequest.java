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
    private String endDate;
    private String appointmentTime;
    private Integer minimumLike;
    private boolean timeReset;
}
