package kr.co.company.capstone.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentCustomDialogBinding

class AlertDialogFragment(
    private val title: String,
    private val body: String,
    private val emojiStatus: Boolean,
    private val positiveButtonText: String = "확인",
    private val negativeButtonText: String? = null,
    private val onPositiveAction: (() -> Unit)? = null,
    private val onNegativeAction: (() -> Unit)? = null

) : DialogFragment(){
    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)

        binding.dialogTitle.text = title
        binding.dialogBody.text = body

        setEmoji(binding.emoji)
        setButtonVisibilityAndListeners()
        return binding.root
    }

    private fun setEmoji(emoji: ImageView) {
        val emojiResource = if (emojiStatus) R.drawable.emojis_smiling_face else R.drawable.emojis_frowning_face
        emoji.setImageResource(emojiResource)
    }

    private fun setButtonVisibilityAndListeners() {
        if (negativeButtonText == null) {
            binding.btnOk.visibility = View.VISIBLE
            binding.buttonContainer.visibility = View.GONE

            binding.btnOk.text = positiveButtonText
            binding.btnOk.setOnClickListener {
                dismiss()
                onPositiveAction?.invoke()
            }
        } else {
            // 두 개의 버튼 표시
            binding.btnOk.visibility = View.GONE
            binding.buttonContainer.visibility = View.VISIBLE

            // 취소 버튼 설정
            binding.btnSelectCancel.text = negativeButtonText
            binding.btnSelectCancel.setOnClickListener {
                dismiss()
                onNegativeAction?.invoke()
            }

            // 확인 버튼 설정
            binding.btnSelectOk.text = positiveButtonText
            binding.btnSelectOk.setOnClickListener {
                dismiss()
                onPositiveAction?.invoke()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}