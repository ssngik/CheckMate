package kr.co.company.capstone.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kr.co.company.capstone.databinding.FragmentInviteUserBinding
import kr.co.company.capstone.dto.ErrorMessage
import kr.co.company.capstone.dto.team_mate.TeamMateInviteRequest
import kr.co.company.capstone.service.TeamMateService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Retrofit
class InviteUserFragment : Fragment() {
    private val args by navArgs<InviteUserFragmentArgs>()

    private var _binding: FragmentInviteUserBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInviteUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnInvite.setOnClickListener { callInviteTeamMateApi() }
        binding.btnXClose.setOnClickListener { parentFragmentManager.popBackStack()}
    }

    private fun callInviteTeamMateApi(){
        val inviteRequest = TeamMateInviteRequest(binding.inputTeamNickname.text.toString())
        var dialogTitle : String
        var dialogBody : String
        var emoji : Boolean
        TeamMateService.service().invite(args.goalId, inviteRequest)
            .enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code()==200) {
                        dialogTitle = "전송 완료"
                        dialogBody = setDialogBodyMessageByCode("success")
                        emoji = true
                    }
                    else { // 404
                        dialogTitle = "초대 실패"
                        dialogBody = setDialogBodyMessageByCode(ErrorMessage.getErrorByResponse(response).code)
                        emoji = false
                    }

                    val dialog = AlertDialogFragment(dialogTitle, dialogBody, emoji)
                    dialog.show(childFragmentManager, "custom")
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    dialogTitle = "서버 오류"
                    dialogBody = setDialogBodyMessageByCode("onFailure")
                }
            })
    }

    private fun setDialogBodyMessageByCode(em : String):String{
        return when(em){
            "USER-001" -> "존재하지 않는 유저입니다."
            "MATE-002" -> "이미 해당 목표를 진행 중인 유저입니다."
            "MATE-003" -> "이미 초대를 보낸 유저입니다."
            "onFailure" -> "통신 오류가 발생했습니다.\n메인화면으로 이동합니다."
            "success" -> "초대 요청을 보냈어요!\n응답이 오면 알려드릴게요"
            else -> "닉네임을 입력해주세요"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}