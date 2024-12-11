package kr.co.company.capstone.post.presenter

import android.content.Context
import android.net.Uri
import kr.co.company.capstone.post.contract.PostContract
import kr.co.company.capstone.post.model.PostModel

class PostPresenter(private var view: PostContract.View?) : PostContract.Presenter {

    private val model = PostModel()

    override fun onTextChanged(isTextNotEmpty: Boolean) {
        view?.updatePostButtonState(isTextNotEmpty)
        view?.updateUnderlineState(isTextNotEmpty)
    }

    override fun registerPost(mateId: Long, content: String, imageUris: List<Uri>?, context: Context) {
        view?.showProgress()
        model.registerPost(
            mateId, content, imageUris, context,
            onSuccess = {
                view?.hideProgress()
                view?.onPostRegisterSuccess()
            },
            onFailure = { errorMessage ->
                view?.hideProgress()
                view?.showError(errorMessage)
            }
        )
    }

    override fun detachView() {
        view = null
    }


}