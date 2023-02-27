package kr.co.company.capstone.dto.login;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NaverAuthModel {
    private String authToken;
    private String fcmToken;

    public NaverAuthModel(String authToken) {
        this.authToken = authToken;
    }

    public NaverAuthModel() {
    }

    public String getAuthToken() {
        return authToken;
    }
}
