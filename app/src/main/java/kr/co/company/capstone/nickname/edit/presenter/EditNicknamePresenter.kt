package kr.co.company.capstone.nickname.edit.presenter

import kr.co.company.capstone.R
import kr.co.company.capstone.nickname.NicknameValidator
import kr.co.company.capstone.nickname.edit.contract.EditNicknameContract
import kr.co.company.capstone.nickname.edit.model.EditNicknameRepository

class EditNicknamePresenter(
    private var view: EditNicknameContract.View,
    private val repository: EditNicknameRepository
) : EditNicknameContract.Presenter {
    override fun onNicknameTextChanged(nickname: String) {
        if (nickname.isEmpty()) {
            view.setDupCheckButtonEnabled(false)
            view.setNicknameEditBackground(false)
            view.setChangeButtonEnabled(false)
            view.setAlertNicknameValidation(null)
            return
        }

        if (NicknameValidator.isValidNickname(nickname)) { // 사용 가능한 닉네임
            view.setNicknameEditBackground(true)
            view.setDupCheckButtonEnabled(true)
            view.setChangeButtonEnabled(false)
            view.setAlertNicknameValidation(R.drawable.nickname_check_ok)
        }else {
            val validationDrawable = NicknameValidator.getValidationDrawable(nickname)
            view.setNicknameEditBackground(false)
            view.setDupCheckButtonEnabled(false)
            view.setChangeButtonEnabled(false)
            view.setAlertNicknameValidation(validationDrawable)

        }
    }

    override fun onDupCheckButtonClicked(nickname: String) {
        repository.checkNicknameExists(nickname,
            onSuccess = { isAvailable ->
                if (isAvailable) {
                    view.showNicknameValidityDialog("확인!", "사용 가능한 닉네임이에요.", true) // 사용 가능 다이얼로그
                    view.setChangeButtonEnabled(true) // 닉네임 변경 버튼 활성화
                }else {
                    view.showNicknameValidityDialog("sorry..", "이미 사용 중인 닉네임이에요.", false) // 이미 존재하는 닉네임인 경우
                    view.setChangeButtonEnabled(false)
                }
            },
            onFailure = { error ->
                view.showErrorDialog(error.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        )
    }

    override fun onChangeButtonClicked(nickname: String) {
        repository.updateNickname(
            newNickname = nickname,
            onSuccess = { isAvailable ->
                if (isAvailable) {
                    view.showNicknameUpdateDialog(
                        "닉네임 변경 완료", "새로운 닉네임을 적용했어요!", isAvailable) {
                        view.navigateBackToMyPage()
                    }
                }else {
                    view.showNicknameUpdateDialog("닉네임 변경 실패", "닉네임을 변경할 수 있는 기간이 아니에요.", isAvailable)
                }
            },
            onFailure = { error ->
                view.showErrorDialog(error.message ?: "닉네임 변경에 실패했습니다.")
            }
        )
    }
}