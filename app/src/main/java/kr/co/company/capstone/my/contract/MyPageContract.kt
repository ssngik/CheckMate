package kr.co.company.capstone.my.contract

class MyPageContract {
    interface View {
        fun navigateToEditNickname()
    }
    interface Presenter {
        fun onEditNicknameClicked()
    }
}