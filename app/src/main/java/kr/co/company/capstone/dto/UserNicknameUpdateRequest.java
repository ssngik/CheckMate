package kr.co.company.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserNicknameUpdateRequest {
    private String nickname;
}
