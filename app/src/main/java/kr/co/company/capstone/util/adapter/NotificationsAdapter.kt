package kr.co.company.capstone.util.adapter

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.NotificationItemBinding
import kr.co.company.capstone.dto.notification.NotificationDetailResponse
import kr.co.company.capstone.dto.notification.NotificationUserResponse
import kr.co.company.capstone.fragment.ErrorDialogFragment
import kr.co.company.capstone.invitationReplyDialog.InvitationReplyDialogFragment
import kr.co.company.capstone.service.NotificationService
import kr.co.company.capstone.util.NavigationUtil
import kr.co.company.capstone.util.time.TimeAgoConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsAdapter(private val fragment : Fragment, private val notifications : List<NotificationUserResponse>):
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 새로 생성된 뷰를 담는 Viewholder 객체를 반환
        return NotificationViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {
        // data
        val notifications = notifications[position]
        holder.bind(notifications)

        // 이미 확인되지 않은 알림인 경우
        if (!notifications.checked){
            holder.itemView.setOnClickListener {
                loadNotificationDetail(holder, notifications.notificationId)
            }
        }
    }

    // 전체 알림 불러오기
    private fun loadNotificationDetail(holder: NotificationViewHolder, notificationId: Long) {
        NotificationService.service().loadSpecificNotificationInformation(notificationId).enqueue(object :
                Callback<NotificationDetailResponse> {
                override fun onResponse(
                    call: Call<NotificationDetailResponse>,
                    response: Response<NotificationDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val notificationDetail = response.body()
                        notificationDetail?.let{handleNotificationDetail(holder, it, notificationId)}
                    }else{
                        showError("알림을 불러올 수 없습니다.")
                    }
                }

                override fun onFailure(call: Call<NotificationDetailResponse>, t: Throwable) {
                    showError("네트워크 문제가 발생 했습니다. 다시 시도해 주세요.")
                }

            })
    }


    private fun handleNotificationDetail(
        holder : NotificationViewHolder, notificationDetail : NotificationDetailResponse, notificationId : Long){
        val attributes: Map<String, String> = Gson().fromJson(notificationDetail.attributes, Map::class.java) as Map<String, String>
        when (notificationDetail.type){
            "INVITE_SEND" -> showInvitationReplyDialog(holder.binding.root, notificationDetail, notificationId)
            "INVITE_ACCEPT" -> actionToTimeLine(holder.binding.root, attributes)
            "POST_UPLOAD" -> actionToTimeLine(holder.binding.root)
            "INVITE_REJECT", "EXPULSION_GOAL" -> holder.binding.notificationUnChecked.visibility = View.INVISIBLE
        }
    }

    // 목표 상세 화면으로 이동
    private fun actionGoalDetail(view: View, goalId : Long){
        val goalDetailBundle = Bundle()
        goalDetailBundle.putLong("goalId", goalId)
        NavigationUtil.navigateTo(view, R.id.action_navigation_notification_to_goalDetailFragment, goalDetailBundle)
    }

    // 타임라인으로 이동
    private fun actionToTimeLine(view:  View, attributes : Map<String, String>?=null){
        val timeLineBundle = Bundle()
        attributes?.get("goalId")?.toLong()?.let{
            timeLineBundle.putLong("goalId", it)
        }
        NavigationUtil.navigateTo(view, R.id.action_navigation_notification_to_timeLineFragment, timeLineBundle)
    }

    // 초대에 대한 응답 Dialog
    private fun showInvitationReplyDialog(view : View, notificationDetail: NotificationDetailResponse?, notificationId: Long) {
        val dialogFragment = InvitationReplyDialogFragment()

        dialogFragment.arguments = Bundle().apply {
            putString("messageBody", notificationDetail?.content)
            putLong("notificationId", notificationId)
        }

        // 다이얼로그 프래그먼트 표시
            dialogFragment.setDialogListener(object : InvitationReplyDialogFragment.CustomDialogListener{
                override fun onPositiveClicked(goalId : Long) {
                    actionGoalDetail(view, goalId)
                }
            })
            dialogFragment.show(fragment.childFragmentManager, "InvitationReplyDialogFragment")

    }

    private fun showError(errorMessage : String){
        ErrorDialogFragment.getErrorMessage(errorMessage).show(fragment.childFragmentManager, "error_dialog")
    }
    override fun getItemCount(): Int = notifications.size
    class NotificationViewHolder(private val context : Context, val binding:NotificationItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(notification : NotificationUserResponse){
            binding.notificationContent.text = getSpannableGoalTitle(notification.content, notification.type)
            binding.notificationTitle.text = notification.title
            binding.notificationDate.text = TimeAgoConverter.formatTimeAgo(notification.sendAt)
            // 확인한 알림인 경우 새 알림 표시 제거
            if (notification.checked) binding.notificationUnChecked.visibility = View.INVISIBLE
        }

        // goal title 색상 변경
        private fun getSpannableGoalTitle(content : String, type: String): SpannableString{
            val goalTitle = extractGoalTitle(content, type)

            val startIndex = content.indexOf(goalTitle)
            val endIndex = startIndex + goalTitle.length
            val spannableGoalTitle = SpannableString(content)
            spannableGoalTitle.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.checkmate_color)),
                startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableGoalTitle
        }

        // content에서 goal title 추출
        private fun extractGoalTitle(content: String, type: String): String {
            return when (type) {
                "INVITE_SEND" -> extractOnInviteSend(content)
                "EXPULSION_GOAL" -> extractOnExpulsionGoal(content)
                "INVITE_REJECT" -> extractOnInviteReject(content)
                "INVITE_ACCEPT" -> extractOnInviteAccept(content)
                "POST_UPLOAD" -> extractOnPostUpload(content)
                else -> ""
            }
        }

        private fun extractOnInviteSend(content: String): String {
            val pattern = "(?<=님이 ).*?(?= 목표로 초대했습니다!)".toRegex()
            return pattern.find(content)?.value ?: ""
        }

        private fun extractOnExpulsionGoal(content: String): String {
            val pattern = ".*(?= 목표에서 퇴출되었습니다.)".toRegex()
            return pattern.find(content)?.value ?: ""
        }

        private fun extractOnInviteReject(content: String): String {
            val pattern = "(?<=님이 ).*?(?=목표로 합류를 거절했어요)".toRegex()
            return pattern.find(content)?.value ?: ""
        }

        private fun extractOnInviteAccept(content: String): String {
            val pattern = "(?<=님이 ).*?(?=목표로 합류했어요!)".toRegex()
            return pattern.find(content)?.value ?: ""
        }

        private fun extractOnPostUpload(content: String): String {
            val pattern = "(?<=의 ).*?(?=님이 목표 수행을 인증했어요!)".toRegex()
            return pattern.find(content)?.value ?: ""
        }

    }

}