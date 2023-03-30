package kr.co.company.capstone.dto.login;

import lombok.*;

@Getter
public class LoginRequest {
    private final String identifier;
    private final String fcmToken;

    @Builder
    public LoginRequest(String identifier, String fcmToken){
        this.identifier = identifier;
        this.fcmToken = fcmToken;
    }
}

