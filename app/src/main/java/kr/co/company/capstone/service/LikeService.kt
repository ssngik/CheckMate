package kr.co.company.capstone.service

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface LikeService {
    // 좋아요
    @POST("/goals/{goalId}/posts/{postId}/like")
    fun like(@Path("goalId") goalId: Long, @Path("postId") postId: Long): Call<Void>

    // 좋아요 취소
    @DELETE("/goals/{goalId}/posts/{postId}/unlike")
    fun unlike(@Path("goalId") goalId: Long, @Path("postId") postId: Long): Call<Void>

    companion object {

        fun getService() : LikeService {
            return RetrofitBuilder.retrofit.create(LikeService::class.java)
        }
    }
}
