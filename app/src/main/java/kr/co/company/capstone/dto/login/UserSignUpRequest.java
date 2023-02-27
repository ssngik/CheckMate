package kr.co.company.capstone.dto.login;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserSignUpRequest implements Serializable {
    private String providerId;
    private String username;
    private String email;

    private String nickname;

    public UserSignUpRequest(String providerId, String username, String email) {
        this.providerId = providerId;
        this.username = username;
        this.email = email;
    }

    public static UserSignUpRequest of(LoginResponse loginResponse) {
        UserSignUpRequest request = new UserSignUpRequest();
        request.providerId = loginResponse.getProviderId();
        request.username = loginResponse.getUsername();
        request.email = loginResponse.getEmail();
        return request;
    }
}
