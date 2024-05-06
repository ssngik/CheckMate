package kr.co.company.capstone.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.ActivitySignupBinding
import kr.co.company.capstone.fragment.AlertDialogFragment
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.main.MainActivity
class SignUpActivity : AppCompatActivity(), SignUpContract.SignUpView {
    private lateinit var presenter: SignUpContract.Presenter
    private var _binding: ActivitySignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = SignUpPresenter(this, SignUpModel(), applicationContext)
        setupListener()
    }

    override fun showProgress() {
        binding.loading.show()
    }

    override fun hideProgress() {
        binding.loading.hide()
    }

    // MainActivity로 이동
    override fun actionToMainActivity() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 회원가입 버튼 상태 변경
    override fun setNicknameValidityDrawable(isValid: Boolean) {
        // 회원가입 버튼 색상 변경
        val colorResource = if (isValid) R.color.checkmate_color else R.color.btn_disabled_color
        binding.joinButton.setBackgroundColor(ContextCompat.getColor(this, colorResource))

        // 회원가입 버튼 클릭 설정
        binding.joinButton.isClickable = isValid
    }

    // 닉네임 중복 확인 Dialog
    override fun showNicknameValidityDialog(isAvailable: Boolean) {
        val message = if (isAvailable) "사용가능한 닉네임입니다." else "이미 사용중인 닉네임이에요."
        AlertDialogFragment("중복 확인", message, isAvailable).show(supportFragmentManager, "dialog")
    }

    override fun showErrorDialog(errorMessage: String) {
        ErrorDialogFragment.newInstance(errorMessage).show(supportFragmentManager, "error_dialog")
    }

    private fun setJoinButtonDisabled() {
        binding.joinButton.isClickable = false
        binding.joinButton.setBackgroundResource(R.drawable.btn_main_disabled)
    }

    private fun getNicknameCheckCode(s: CharSequence?): SignUpPresenter.NicknameCheckCode {
        val code = presenter.checkNicknameValidity(s.toString())
        binding.requestButton.isClickable = code == SignUpPresenter.NicknameCheckCode.NICKNAME_OK
        return code
    }

    // 닉네임 조건 부합 여부에 따른 문구 UI 설정
    private fun handleNicknameValidationImage(drawable : Int) {
        binding.alertNicknameValidation.setImageResource(drawable)
    }

    private fun setupListener() {
        // 시작하기 버튼
        binding.joinButton.setOnClickListener { presenter.onJoinButtonClicked(binding.putNickname.text.toString()) }
        // 닉네임 중복확인 버튼
        binding.requestButton.setOnClickListener { presenter.checkNicknameDuplicate(binding.putNickname.text.toString()) }
        // 닉네임 input 리스너
        binding.putNickname.addTextChangedListener (nicknameWatcher)
    }

    // 닉네임 input text Changed Listener
    private val nicknameWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 닉네임 조건 부합 여부에 따른 경고 문구 변경
            val code = getNicknameCheckCode(s)
            handleNicknameValidationImage(presenter.getNicknameCheckDrawable(code))

            // 닉네임 입력 여부에 따른 밑줄 색상 변경
            binding.inputLine.setBackgroundResource(presenter.getEditTextUnderLineColor(s))

            // 회원가입 버튼 상태 비활성
            setJoinButtonDisabled()
        }
        override fun afterTextChanged(s: Editable?) {}
    }
}

