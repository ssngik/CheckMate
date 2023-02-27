package kr.co.company.capstone.dto.post;

import java.util.List;

import lombok.Data;

@Data
public class PostListInquiryResponse {
    private List<PostInquiryResponse> posts;
    private Integer minimumLike;
    private String goalTitle;
}
