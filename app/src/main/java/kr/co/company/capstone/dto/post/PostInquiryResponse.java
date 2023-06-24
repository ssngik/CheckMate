package kr.co.company.capstone.dto.post;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data
@Getter
@Setter
@Data
public class PostInquiryResponse {
    private long postId;             // 포스트ID
    private long mateId;         // 업로더의 팀원 ID
    private String uploaderNickname; // 업로더의 닉네임
    private String uploadAt;         // 업로드 시간
    private List<String> imageUrls;  // 이미지 파일 접근 주소
    private String content;          // 텍스트 인증 내용
    private List<Long> likedUserIds;   // 좋아요 누른 유저 ID
}
