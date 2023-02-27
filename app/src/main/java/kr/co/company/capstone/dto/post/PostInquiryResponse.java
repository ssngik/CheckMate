package kr.co.company.capstone.dto.post;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data
@Getter
@Setter
public class PostInquiryResponse {
    private long postId;
    private long teamMateId;
    private String uploaderNickname;
    private String uploadAt;
    private List<String> imageUrls;
    private List<Long> likedUsers;
    private String text;

}
