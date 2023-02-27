package kr.co.company.capstone.util.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryItem {
    private Long goalId;
    private String itemTitle;
    private String startDate;
    private String endDate;
    private double achievement;
    // 하위 리사이클러뷰 아이템으로 정의한 subItemList를 전역변수로 선언한다.
    private List<HistorySubItem> historySubItemList;

}
