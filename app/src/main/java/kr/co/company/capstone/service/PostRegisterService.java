package kr.co.company.capstone.service;

import java.util.ArrayList;
import java.util.Map;

import kr.co.company.capstone.dto.post.PostRegisterResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface PostRegisterService {
    @Multipart @POST("/post")
    Call<PostRegisterResponse> register(@PartMap Map<String, RequestBody> map,
                                        @Part ArrayList<MultipartBody.Part> multipartFiles);

    static PostRegisterService getService(){
        return RetrofitBuilder.getRetrofit().create(PostRegisterService.class);
    }
}