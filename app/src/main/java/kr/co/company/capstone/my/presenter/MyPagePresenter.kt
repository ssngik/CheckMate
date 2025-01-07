package kr.co.company.capstone.my.presenter

import kr.co.company.capstone.my.contract.MyPageContract

class MyPagePresenter(
    private val view : MyPageContract.View,
) : MyPageContract.Presenter {

    override fun onEditNicknameClicked() {
        view.navigateToEditNickname()
    }
}