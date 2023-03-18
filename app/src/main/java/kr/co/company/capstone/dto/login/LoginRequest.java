package kr.co.company.capstone.dto.login;

import lombok.*;

@Getter
@AllArgsConstructor //  모든 필드를 매개변수로 받는 생성자를 자동으로 생성
@NoArgsConstructor // 매개변수가 없는 기본 생성자를 자동으로 생성

public class LoginRequest {
    private String providerId;
    private String fcmToken;
}

