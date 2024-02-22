package kr.co.company.capstone.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentCustomDialogBinding

class CustomDialog(val title: String, val body: String) : DialogFragment(){
    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)

        binding.errorTitle.text = title
        binding.errorBody.text = body

        setButtonClickListener()

        return binding.root
    }

    private fun setButtonClickListener() {
        binding.goToMain.setOnClickListener {
            parentFragment?.let { parentFragment ->
                Navigation.findNavController(
                    parentFragment.requireView()
                ).navigate(R.id.action_inviteUserFragment_to_navigation_home)
            }
        }

        binding.inviteMore.setOnClickListener { dismiss() }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}