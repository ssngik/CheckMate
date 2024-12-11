package kr.co.company.capstone.post.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kr.co.company.capstone.databinding.FragmentPostBinding
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.post.contract.PostContract
import kr.co.company.capstone.post.presenter.PostPresenter

class PostFragment : Fragment(), PostContract.View {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val args: PostFragmentArgs by navArgs()
    private lateinit var presenter: PostContract.Presenter

    private var selectedImageUris: List<String>? = null
    private var mateId: Long = 0L
    private var goalId: Long = 0L
    private var goalTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = PostPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        processArgs()
        initListener()
        initUi()
    }
    private fun initUi() {
        binding.btnPost.isEnabled = false
        binding.goalTitle.text = goalTitle
        showSelectedImages(selectedImageUris ?: emptyList())
    }

    private fun processArgs() {
        goalId = args.goalId
        mateId = args.mateId
        goalTitle = args.goalTitle
        selectedImageUris = args.selectedUris.toList()
    }

    private fun initListener() {
        // Text 입력 감지
        binding.postTextInput.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isInputExist = !s.isNullOrEmpty()
                presenter.onTextChanged(isInputExist)
            }
        })

        // 게시물 등록 버튼 클릭 리스너
        binding.btnPost.setOnClickListener {
            val content = binding.postTextInput.text.toString()
            val imageUris = selectedImageUris?.map { uriString ->
                Uri.parse(uriString)
            }
            presenter.registerPost(mateId, content, imageUris, requireContext())
        }
    }

    private fun showSelectedImages(selectedUris: List<String>) {

        // 선택된 이미지 초기화. 최대 3장
        binding.imageFirst.setImageDrawable(null)
        binding.imageSecond.setImageDrawable(null)
        binding.imageThird.setImageDrawable(null)

        selectedUris.forEachIndexed { index, uriString ->
            val uri = Uri.parse(uriString)
            when (index) {
                0 -> binding.imageFirst.setImageURI(uri)
                1 -> binding.imageSecond.setImageURI(uri)
                2 -> binding.imageThird.setImageURI(uri)
            }
        }
    }


    // 게시 버튼 상태 업데이트
    override fun updatePostButtonState(isEnabled: Boolean) {
        binding.btnPost.isEnabled = isEnabled
    }

    // 사용자 게시글 input 밑줄 상태 업데이트
    override fun updateUnderlineState(isSelected: Boolean) {
        binding.postPageLine2.isSelected = isSelected
    }

    override fun showError(errorMessage: String) {
        val errorDialog = ErrorDialogFragment.getErrorMessage(errorMessage)
        errorDialog.show(childFragmentManager, "error_dialog")
    }

    override fun showProgress() {
        binding.postLoading.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.postLoading.visibility = View.INVISIBLE
    }

    // 게시물 등록 성공
    override fun onPostRegisterSuccess() {
        val action = PostFragmentDirections.actionPostFragmentToTimeLineFragment(goalId)
        findNavController().navigate(action)
    }

    override fun setGoalTitle(goalTitle: String) {
        binding.goalTitle.text = goalTitle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.detachView()
    }
}