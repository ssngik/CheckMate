package kr.co.company.capstone.dto.login;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String providerId;
    private String username;
    private String email;
}
