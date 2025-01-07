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
    val title: String,
    val body: String,
    private val emojiStatus: Boolean,
    private val onPositiveAction: (() -> Unit)? = null
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
        setButtonClickListener()
        return binding.root
    }

    private fun setEmoji(emoji: ImageView) {
        if (emojiStatus) emoji.setImageResource(R.drawable.emojis_smiling_face)
        else emoji.setImageResource(R.drawable.emojis_frowning_face)
    }

    private fun setButtonClickListener() {
        binding.btnOk.setOnClickListener {
            onPositiveAction?.invoke()
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}