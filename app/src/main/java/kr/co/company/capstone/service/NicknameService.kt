package kr.co.company.capstone.service

import kr.co.company.capstone.dto.user.UserNicknameUpdate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface NicknameService {
    @GET("/users/exists")
    fun checkNicknameExists(@Query("nickname") nickname: String): Call<Void>

    @PATCH("/users/nickname")
    fun updateNickname(@Body request: UserNicknameUpdate): Call<Void>}