package kr.co.company.capstone.dto.goal;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Uploadable {
    private boolean uploadable;
    private boolean uploaded;
    private boolean workingDay;
    private boolean timeOver;
}
