package kr.co.company.capstone.nickname.edit.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentEditNicknameBinding
import kr.co.company.capstone.fragment.AlertDialogFragment
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.nickname.edit.contract.EditNicknameContract
import kr.co.company.capstone.nickname.edit.model.EditNicknameRepositoryImpl
import kr.co.company.capstone.nickname.edit.presenter.EditNicknamePresenter

class EditNicknameFragment : Fragment(), EditNicknameContract.View  {
    private var _binding: FragmentEditNicknameBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter : EditNicknameContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EditNicknamePresenter(this, EditNicknameRepositoryImpl(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 네비게이션 바 숨기기
        val bottomNav = requireActivity().findViewById<View>(R.id.bottomNav_view)
        bottomNav.visibility = View.GONE

        initListener()
    }

    private fun initListener() {
        // 닉네임 입력 TextWatcher
        binding.putNickname.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.onNicknameTextChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 중복확인 버튼
        binding.requestDupCheckButton.setOnClickListener {
            val nickname = binding.putNickname.text.toString()
            presenter.onDupCheckButtonClicked(nickname)
        }

        // 닉네임 변경 버튼
        binding.editButton.setOnClickListener {
            val nickname = binding.putNickname.text.toString()
            presenter.onChangeButtonClicked(nickname)
        }
    }

    override fun showNicknameValidityDialog(title: String, body: String, isAvailable: Boolean) {
        AlertDialogFragment(title, body, isAvailable).show(parentFragmentManager, "nickname_dialog")
    }

    override fun showNicknameUpdateDialog(
        title: String,
        body: String,
        isAvailable: Boolean,
        onPositiveAction: (() -> Unit)?
    ) {
        AlertDialogFragment(title, body, isAvailable, onPositiveAction).show(parentFragmentManager, "nickname_dialog")
    }

    override fun showErrorDialog(errorMessage: String) {
        ErrorDialogFragment.getErrorMessage(errorMessage).show(parentFragmentManager, "error_dialog")
    }

    override fun setNicknameEditBackground(isValid: Boolean) {
        binding.inputLine.isSelected = isValid
    }

    override fun setDupCheckButtonEnabled(enable: Boolean) {
        binding.requestDupCheckButton.isEnabled = enable
    }

    override fun setChangeButtonEnabled(enable: Boolean) {
        binding.editButton.isEnabled = enable
    }

    override fun setAlertNicknameValidation(drawableRes: Int?) {
        if (drawableRes != null) {
            binding.alertNicknameValidation.setImageResource(drawableRes)
            binding.alertNicknameValidation.visibility = View.VISIBLE
        } else {
            binding.alertNicknameValidation.visibility = View.GONE
        }
    }

    override fun navigateBackToMyPage() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val bottomNav = requireActivity().findViewById<View>(R.id.bottomNav_view)
        bottomNav.visibility = View.VISIBLE

        _binding = null
    }

}