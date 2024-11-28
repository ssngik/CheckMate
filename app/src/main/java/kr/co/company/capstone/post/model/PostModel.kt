package kr.co.company.capstone.post.model

import android.content.Context
import android.net.Uri
import android.util.Log
import kr.co.company.capstone.dto.post.PostResponse
import kr.co.company.capstone.service.PostService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostModel {

    private val postService = PostService.getService()

    fun registerPost(
        mateId: Long,
        content: String,
        imageUris: List<Uri>?,
        context: Context,
        onSuccess: (PostResponse) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // parameter : RequestBody 생성
        val params = HashMap<String, RequestBody>().apply {
            put("mateId", RequestBody.create("text/plain".toMediaTypeOrNull(), mateId.toString()))
            put("content", RequestBody.create("text/plain".toMediaTypeOrNull(), content))
        }

        // 이미지 파일을 MultipartBody.Part로 변환
        val multipartFiles = ArrayList<MultipartBody.Part>()
        imageUris?.forEachIndexed { index, uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            val requestFile = bytes?.let {
                RequestBody.create("image/*".toMediaTypeOrNull(), it)
            }
            requestFile?.let {
                // parameter 이름 : imageFile1, imageFile2...
                val part = MultipartBody.Part.createFormData(
                    "imageFile${index + 1}",
                    "image${index + 1}.jpg",
                    it
                )
                multipartFiles.add(part)
            }
        }

        // API 호출
        val call = postService.register(params, multipartFiles)

        call.enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: Response<PostResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onFailure("게시물 등록에 실패했습니다\n. 에러: $errorBody")
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                onFailure("네트워크 오류가 발생했습니다.")
            }
        })
    }
}