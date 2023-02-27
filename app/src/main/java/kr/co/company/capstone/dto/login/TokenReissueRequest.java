package kr.co.company.capstone.dto.login;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenReissueRequest {
    private String accessToken;
    private String refreshToken;
}
