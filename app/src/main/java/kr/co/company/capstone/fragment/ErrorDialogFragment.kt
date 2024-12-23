package kr.co.company.capstone.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.company.capstone.databinding.ErrorDialogLayoutBinding


class ErrorDialogFragment : DialogFragment() {

    private var _binding: ErrorDialogLayoutBinding? = null
    private val binding get() = _binding!!
    private var positiveClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ErrorDialogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val errorMessage = arguments?.getString("errorMessage") ?: "ERROR"

        binding.dialogBody.text = errorMessage

        // 완료 버튼 클릭 시.
        binding.positiveButton.setOnClickListener {
            positiveClickListener?.invoke()
            dismiss()
        }
    }

    fun setPositiveClickListener(listener: () -> Unit) {
        positiveClickListener = listener
    }

    companion object {
        @JvmStatic
        fun getErrorMessage(errorMessage: String) : ErrorDialogFragment{
            val fragment = ErrorDialogFragment()
            val args = Bundle()
            args.putString("errorMessage", errorMessage)
            fragment.arguments = args
            return fragment
        }

    }
}