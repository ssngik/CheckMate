package kr.co.company.capstone.post.contract

import android.content.Context
import android.net.Uri

interface PostContract {
    interface View {
        fun updatePostButtonState(isEnabled: Boolean)
        fun updateUnderlineState(isSelected: Boolean)
        fun showError(errorMessage: String)
        fun showProgress()
        fun hideProgress()
        fun onPostRegisterSuccess()
        fun setGoalTitle(goalTitle: String)

    }

    interface Presenter {
        fun registerPost(mateId: Long, content: String, imageUris: List<Uri>?, context: Context)
        fun onTextChanged(isTextNotEmpty: Boolean)
        fun detachView()
    }
}