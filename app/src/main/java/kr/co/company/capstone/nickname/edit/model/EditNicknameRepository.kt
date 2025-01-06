package kr.co.company.capstone.nickname.edit.model

interface EditNicknameRepository {

    // 중복 확인
    fun checkNicknameExists(
        nickname: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    )

    // 닉네임 변경
    fun updateNickname(
        newNickname: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Throwable) -> Unit
    )
}