package kr.co.company.capstone.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NotificationDetailResponse implements Serializable {
    private String title; // 알림 타이틀
    private String content; // 알림 내용
    private String type; // 알림 종류
    private String attributes; // 해당 알림에 필요한 추가 데이터 - JSON 형식
}
