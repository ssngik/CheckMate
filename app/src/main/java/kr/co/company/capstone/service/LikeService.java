package kr.co.company.capstone.service;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikeService {
    @POST("/post/{postId}/like")
    Call<Void> like(@Path("postId")long postId);

    @DELETE("/post/{postId}/unlike")
    Call<Void> unlike(@Path("postId")long postId);

    static LikeService getService(){
        return RetrofitBuilder.getRetrofit().create(LikeService.class);
    }
}
