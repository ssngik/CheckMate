package kr.co.company.capstone.invitationReplyDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.FragmentInvitationReplyBinding

class InvitationReplyDialogFragment : DialogFragment(), InvitationReplyDialogContract.InvitationReplyView {
    private var _binding: FragmentInvitationReplyBinding? = null
    private val binding get() = _binding!!
    private var listener: CustomDialogListener? = null
    private lateinit var presenter : InvitationReplyDialogContract.InvitationReplyPresenter
    private var message: String? = null
    private var notificationId: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            message = bundle.getString("messageBody")
            notificationId = bundle.getLong("notificationId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvitationReplyBinding.inflate(inflater, container, false)
        presenter = InvitationReplyDialogPresenter(this, InvitationReplyDialogModel())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding){
            btnReplyAccept.setOnClickListener { onPositiveButtonClicked(notificationId) } // 초대 수락
            btnReplyReject.setOnClickListener { onNegativeButtonClicked(notificationId) } // 초대 거부
            btnReplyException.setOnClickListener { dismiss() } // 합류 실패한 경우 버튼
            replyDialogContent.text = message
        }
    }

    fun setDialogListener(customDialogListener: CustomDialogListener) {
        this.listener = customDialogListener
    }


    // 목표 합류 가능 여부에 따른 Dialog 처리
    private fun setButtonStatus(title: String, content: String) {
        binding.replyDialogContent.text = content
        binding.replyDialogTitle.text = title
        binding.replyEmoji.setImageResource(R.drawable.emojis_frowning_face)
        binding.invitationBtnGroup.visibility = View.GONE
        binding.btnReplyException.visibility = View.VISIBLE
    }

    // 초대 수락 버튼
    private fun onPositiveButtonClicked(notificationId : Long) {
        presenter.onAcceptInvitation(notificationId)
    }

    // 초대 거절 버튼
    private fun onNegativeButtonClicked(notificationId : Long) {
        presenter.onRejectInvitation(notificationId)
    }

    // 팀원이 초대를 수락한 경우
    override fun onInvitationAccepted(goalId: Long) {
        listener?.onPositiveClicked(goalId)
        dismiss()
    }

    // 팀원이 초대를 거절한 경우
    override fun onInvitationRejected() {
        dismiss()
    }

    // 합류 기간이 지나거나, 통신 문제 발생
    override fun onInvitationFailed(title: String, content: String) {
        setButtonStatus(title, content)
    }

    // 목표 상세로 action 처리
    interface CustomDialogListener {
        fun onPositiveClicked(goalId: Long)
    }

}
