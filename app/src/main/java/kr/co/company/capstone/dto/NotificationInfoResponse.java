package kr.co.company.capstone.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationInfoResponse implements Serializable {
    private Long notificationId;
    private String title;
    private String body;
    private boolean checked;
    private String sendAt;
    private String type;
}
