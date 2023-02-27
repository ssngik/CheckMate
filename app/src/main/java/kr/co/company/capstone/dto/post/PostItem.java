package kr.co.company.capstone.dto.post;

import java.util.ArrayList;
import java.util.List;

public class PostItem {

    // 상위 리사이클러뷰 아이템
    private long postId;
    private String username;
    private String text;
    private int likeCount;
    private boolean liked;
    private String uploadAt;

    // 하위 리사이클러뷰 아이템으로 정의한 subItemList 전역변수로 선언해야 함
    private ArrayList<PostSubItem> postSubItemList;

    public PostItem(long postId, String username, String text,  int likeCount, boolean liked, String uploadAt, ArrayList<PostSubItem> postSubItemList){
        this.postId = postId;
        this.username = username;
        this.text = text;
        this.likeCount = likeCount;
        this.liked = liked;
        this.uploadAt = uploadAt;
        this.postSubItemList = postSubItemList;

    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public String getUploadAt() {
        return uploadAt;
    }

    public void setUploadAt(String uploadAt) {
        this.uploadAt = uploadAt;
    }

    public ArrayList<PostSubItem> getPostSubItemList() {
        return postSubItemList;
    }

    public void setPostSubItemList(ArrayList<PostSubItem> postSubItemList) {
        this.postSubItemList = postSubItemList;
    }



}

