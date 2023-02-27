package kr.co.company.capstone.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationInfoListResponse {
    private List<NotificationInfoResponse> values;
    private boolean hasNext;
}
