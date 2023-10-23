package kr.co.company.capstone.dto.notification;

import java.io.Serializable;

import lombok.*;

@Data
public class NotificationInfoResponse  {
    private Long notificationId; // 알림 ID
    private String title; // 알림 타이틀
    private String content; // 알림 내용
    private boolean isRead; // 알림 수신 여부
    private String sendAt; // 알림 전송 날짜, 시간
    private String type; // 알림 종류
}
