package kr.co.company.capstone.dto.login;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

// setter 지양하기
// setter 지우고 생성자로 주입
// mapper to request
// NoArgs
// getter/Allarg
// Builder 사용

public class UserSignUpRequest implements Serializable{
    private String providerId;
    private String username;
    private String emailAddress;

    private String nickname;
    private String fcmToken;
    // RequestFields -> 회원가입 API 문서
    public UserSignUpRequest(String providerId, String username, String email, String nickname, String fcmToken) {
        this.providerId = providerId;
        this.username = username;
        this.emailAddress = email;
        this.nickname=nickname;
        this.fcmToken = fcmToken;
    }

    public static UserSignUpRequest of(LoginResponse loginResponse) {
        UserSignUpRequest request = new UserSignUpRequest();
        request.providerId = loginResponse.getProviderId();
        request.username = loginResponse.getUsername();
        request.emailAddress = loginResponse.getEmail();
        return request;
    }
}
