package kr.co.company.capstone.dto.login;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

// TODO: 2023/03/24 Builder 사용
public class UserSignUpRequest implements Serializable{

    private String userIdentifier;
    private String username;
    private String emailAddress;
    private String nickname;
        public UserSignUpRequest(String userIdentifier, String username, String email, String nickname) {
        this.userIdentifier = userIdentifier;
        this.username = username;
        this.emailAddress = email;
        this.nickname=nickname;
    }
}
