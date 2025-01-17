package kr.co.company.capstone.invite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kr.co.company.capstone.databinding.FragmentInviteUserBinding
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest
import kr.co.company.capstone.fragment.AlertDialogFragment
import kr.co.company.capstone.invite.contract.InviteUserContract
import kr.co.company.capstone.invite.model.InviteUserRepositoryImpl
import kr.co.company.capstone.invite.presenter.InviteUserPresenter
import kr.co.company.capstone.service.TeamMateService

class InviteUserFragment : Fragment(), InviteUserContract.View {
    private val args by navArgs<InviteUserFragmentArgs>()

    private var _binding: FragmentInviteUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: InviteUserContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = InviteUserRepositoryImpl(TeamMateService.service())
        presenter = InviteUserPresenter(repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.attachView(this)
        initListener()
    }

    private fun initListener() {
        // 초대 버튼 클릭
        binding.btnInvite.setOnClickListener{
            val nickname = binding.inputTeamNickname.text.toString()
            val request = TeamMateInviteRequest(inviteeNickname = nickname)
            presenter.onInviteButtonClicked(args.goalId, request)
        }
        // 나가기 버튼
        binding.btnXClose.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    override fun showInvitationResultDialog(
        title: String,
        body: String,
        emoji: Boolean,
        positiveButtonText: String,
        onPositiveAction: () -> Unit,
        negativeButtonText: String?,
        onNegativeAction: (() -> Unit)?
    ) {
        val dialog = AlertDialogFragment(
            title = title,
            body = body,
            emojiStatus = emoji,
            positiveButtonText = positiveButtonText,
            onPositiveAction = { onPositiveAction.invoke() },  // 다이얼로그 닫기
            negativeButtonText = negativeButtonText,
            onNegativeAction = { onNegativeAction?.invoke() } // 화면 전환
        )
        dialog.show(childFragmentManager, "invite_result_dialog")
    }


    // 뒤로 가기
    override fun navigateToPreviousScreen() {
        parentFragmentManager.popBackStack()
    }

    // Home으로 이동
    override fun navigateToHome() {
        val action = InviteUserFragmentDirections.actionInviteUserFragmentToNavigationHome()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
        _binding=null
    }
}