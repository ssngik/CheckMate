package kr.co.company.capstone.dto;

import java.util.List;

import lombok.Data;

@Data
public class NotificationDetailListResponse {
    List<NotificationDetailResponse> notifications;
}
