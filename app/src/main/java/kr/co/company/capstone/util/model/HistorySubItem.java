package kr.co.company.capstone.util.model;

// 자식 아이템
public class HistorySubItem {
    private String subItemTitle;
    private String subItemDesc;

    public HistorySubItem(String subItemTitle, String subItemDesc) {
        this.subItemTitle = subItemTitle;
        this.subItemDesc = subItemDesc;
    }


    public String getSubItemTitle() {
        return subItemTitle;
    }

}
