package kr.co.company.capstone.dto.goal;

import java.util.List;

import lombok.Data;


@Data
public class GoalInfoListResponse<T> {
    private List<T> goals;
}
