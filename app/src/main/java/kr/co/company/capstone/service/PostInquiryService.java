package kr.co.company.capstone.service;

import kr.co.company.capstone.dto.post.PostListInquiryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostInquiryService {
    @GET("/post")
    Call<PostListInquiryResponse> getPosts(@Query("goalId") long goalId, @Query("date") String date);

    static PostInquiryService getService(){
        return RetrofitBuilder.getRetrofit().create(PostInquiryService.class);
    }
}
