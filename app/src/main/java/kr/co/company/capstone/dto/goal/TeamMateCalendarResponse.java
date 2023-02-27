package kr.co.company.capstone.dto.goal;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeamMateCalendarResponse {
    private String startDate;
    private String goalPeriod;
    private String teamMatePeriod;
}
