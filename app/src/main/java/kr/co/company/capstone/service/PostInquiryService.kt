package kr.co.company.capstone.service

import kr.co.company.capstone.dto.timeline.PostResponse
import kr.co.company.capstone.service.RetrofitBuilder.retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PostInquiryService {

    // 목표의 해당 날짜 게시글 조회
    @GET("/goals/{goalId}/posts/{date}")
    fun getPosts(
        @Path("goalId") goalId: Long,
        @Path("date") date: String
    ): Call<PostResponse>

    companion object {
        fun getService(): PostInquiryService {
            return retrofit.create(PostInquiryService::class.java)
        }
    }
}