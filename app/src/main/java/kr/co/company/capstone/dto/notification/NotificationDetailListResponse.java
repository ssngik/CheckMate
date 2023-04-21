package kr.co.company.capstone.dto.notification;

import java.util.List;

import lombok.Data;

@Data
public class NotificationDetailListResponse {
    List<NotificationDetailResponse> notifications;
}
