package kr.co.company.capstone;

import com.auth0.android.jwt.JWT;

import java.util.Date;

public class JwtTokenDecoder {
    private final String token;
    private JWT jwt;

    public JwtTokenDecoder(String token) {
        this.token = token;
        this.jwt = new JWT(token.replace("Bearer ", ""));
    }

    public long getUserIdByToken() {
        return jwt.getClaim("id").asLong();
    }

    public String getNicknameByToken() {
        return jwt.getClaim("nickname").asString();
    }

    public boolean isTokenExpired() {
        return jwt.getExpiresAt().before(new Date());
    }
}
