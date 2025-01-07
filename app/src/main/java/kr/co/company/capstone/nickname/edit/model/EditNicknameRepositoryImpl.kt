package kr.co.company.capstone.nickname.edit.model

import android.content.Context
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.user.UserNicknameUpdate
import kr.co.company.capstone.service.NicknameService
import kr.co.company.capstone.service.RetrofitBuilder
import kr.co.company.capstone.util.SharedPreferenceUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditNicknameRepositoryImpl(private val context : Context) : EditNicknameRepository{
    private val nicknameService: NicknameService = RetrofitBuilder.retrofit.create(NicknameService::class.java)

    override fun checkNicknameExists(
        nickname: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        nicknameService.checkNicknameExists(nickname).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess(true)
                }else {
                    val errorMessage = ErrorMessage.getErrorByResponse(response)
                    if (errorMessage.code == "USER-003") { // 이미 존재하는 경우
                        onSuccess(false)
                    }else {
                        onFailure(Exception("Unexpected error code: ${errorMessage.code}"))
                    }
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) { onFailure(t) }
        })
    }

    override fun updateNickname(
        newNickname: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val request = UserNicknameUpdate(nickname = newNickname)
        nicknameService.updateNickname(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess(true)
                    saveNicknameToPreferences(newNickname)
                } else {
                    val em = ErrorMessage.getErrorByResponse(response)
                    if (em.code == "C-003") { // 변경 가능 기간이 아닌 경우
                        onSuccess(false)
                    } else {
                        onFailure(Exception(em.code))
                    }
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) { onFailure(t) }
        })
    }

    private fun saveNicknameToPreferences(nickname: String) {
        SharedPreferenceUtil.setString(context, "nickname", nickname)
    }
}