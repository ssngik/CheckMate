package kr.co.company.capstone.dto.login;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GoogleAuthModel {
    private String providerId;
    private String username;
    private String email;
    private String fcmToken;

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Builder
    public GoogleAuthModel(String providerId, String username, String email, String fcmToken) {
        this.providerId = providerId;
        this.username = username;
        this.email = email;
        this.fcmToken = fcmToken;
    }
}
