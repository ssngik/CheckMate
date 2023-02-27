package kr.co.company.capstone.dto.goal;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoalDate {
    private String localDate;
    private boolean workingDay;
    private boolean checked;
}
