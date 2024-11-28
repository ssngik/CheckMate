package kr.co.company.capstone.service

import kr.co.company.capstone.dto.post.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

interface PostService {

    @Multipart
    @POST("/posts")
    fun register(
        @PartMap map: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part multipartFiles: List<MultipartBody.Part>
    ): Call<PostResponse>

    companion object {
        fun getService(): PostService {
            return RetrofitBuilder.retrofit.create(PostService::class.java)
        }
    }
}