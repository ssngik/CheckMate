package kr.co.company.capstone.dto.post;

import java.util.List;

import lombok.Data;

@Data
public class PostListInquiryResponse<T> {
    private List<T> posts;
    private String goalTitle; // 해당 목표의 이름
}
