package kr.co.company.capstone.nickname.edit.contract

class EditNicknameContract {
    interface View {
        fun showNicknameValidityDialog(title: String, body: String, isAvailable: Boolean)
        fun showErrorDialog(errorMessage: String)
        fun showNicknameUpdateDialog(title: String, body: String, isAvailable: Boolean, onPositiveAction: (() -> Unit)? = null)
        fun setAlertNicknameValidation(drawableRes: Int?)
        fun navigateBackToMyPage()
        fun setNicknameEditBackground(isValid: Boolean)
        fun setDupCheckButtonEnabled(enable: Boolean)
        fun setChangeButtonEnabled(enable: Boolean)
    }

    interface Presenter {
        fun onNicknameTextChanged(nickname: String)
        fun onDupCheckButtonClicked(nickname: String)
        fun onChangeButtonClicked(nickname: String)
    }
}