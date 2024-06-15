package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.post.PostInquiryResponse;
import kr.co.company.capstone.dto.post.PostListInquiryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PostInquiryService {

    // 목표의 해 날짜 게시글 조회
    @GET("/goals/{goalId}/posts/{date}")
    Call<PostListInquiryResponse<PostInquiryResponse>> getPosts(@Path("goalId") long goalId, @Path("date") String date);

    static PostInquiryService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(PostInquiryService.class);
    }
}
