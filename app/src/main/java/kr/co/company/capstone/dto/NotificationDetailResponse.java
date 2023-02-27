package kr.co.company.capstone.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NotificationDetailResponse implements Serializable {
    private String title;
    private String body;
    private String notificationType;
    private String attributes;
}
