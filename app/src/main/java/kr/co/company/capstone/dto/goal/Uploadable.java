package kr.co.company.capstone.dto.goal;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Uploadable {
    private boolean uploaded; // 이미 인증했는지
    private boolean uploadable; // 목표를 조회한 유저가 목표를 인증할 수 있는지
    private boolean workingDay; // 인증한 날이 맞는지
    private boolean timeOver; // 인증 시간 초과 여부
}


