package kr.co.company.capstone.dto.notification;

import java.util.List;
import lombok.Data;

@Data
public class NotificationInfoListResponse<T> {
    private List<T> notifications;
    private boolean hasNext; // 조회할 수 있는 다음 페이지가 있는지 여부
}
