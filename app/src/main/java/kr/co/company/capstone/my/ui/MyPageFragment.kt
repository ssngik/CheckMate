package kr.co.company.capstone.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.fragment.app.Fragment
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentMyPageBinding
import kr.co.company.capstone.my.contract.MyPageContract
import kr.co.company.capstone.my.presenter.MyPagePresenter
import kr.co.company.capstone.util.NavigationUtil

class MyPageFragment : Fragment(), MyPageContract.View {

    private var _binding : FragmentMyPageBinding?= null
    private val binding get() = _binding!!

    private lateinit var presenter: MyPageContract.Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = MyPagePresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }
    override fun navigateToEditNickname() {
        NavigationUtil.navigateTo(binding.root, R.id.action_myPageFragment_to_editNicknameFragment)
    }

    private fun initListener() {
        binding.editNickname.setOnClickListener { presenter.onEditNicknameClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}