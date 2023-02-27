package kr.co.company.capstone.dto.login;

import com.kakao.sdk.user.model.User;

public class KakaoAuthModel {
    private String providerId;
    private String username;
    private String email;
    private String fcmToken;

    public KakaoAuthModel(User kakaoAccount) {
        this.providerId = String.valueOf(kakaoAccount.getId());
        this.email = kakaoAccount.getKakaoAccount().getEmail();
        this.username = kakaoAccount.getProperties().get("nickname");
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}