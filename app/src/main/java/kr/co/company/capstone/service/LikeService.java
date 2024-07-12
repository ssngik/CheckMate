package kr.co.company.capstone.service;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikeService {
    // 좋아요
    // /goals/{goalId}/posts/{postId}/like
    @POST("/goals/{goalId}/posts/{postId}/like")
    Call<Void> like(@Path("goalId") long  goalId ,@Path("postId")long postId);

    // 좋아요 취소
    @DELETE("/goals/{goalId}/posts/{postId}/unlike")
    Call<Void> unlike(@Path("goalId") long  goalId, @Path("postId")long postId);

    static LikeService getService(){
        return RetrofitBuilder.INSTANCE.getRetrofit().create(LikeService.class);
    }
}
