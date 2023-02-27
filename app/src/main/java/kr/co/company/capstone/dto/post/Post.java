package kr.co.company.capstone.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Post {
    //private List<PostSubItem> subItemList;
    private long postId;
    private String username;
    private String text;
    //private String photo;
    private int likeCount;
    private boolean liked;
    private String uploadAt;
}


