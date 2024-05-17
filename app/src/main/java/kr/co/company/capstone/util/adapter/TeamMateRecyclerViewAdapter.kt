package kr.co.company.capstone.util.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.TeamMateItemBinding
import kr.co.company.capstone.dto.goal.Mate

class TeamMateRecyclerViewAdapter(private val teamMates: List<Mate>) :
    RecyclerView.Adapter<TeamMateRecyclerViewAdapter.TeamMateViewHolder>() {
    override fun getItemCount(): Int {
        return teamMates.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMateViewHolder {
        val binding = TeamMateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamMateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeamMateViewHolder, position: Int) {
        // 팀원의 닉네임
        val nickname = teamMates[position].nickname
        // 팀원의 목표 수행 인증 상태
        val status = teamMates[position].uploaded

        holder.bind(nickname, status)
    }

    inner class TeamMateViewHolder(val binding : TeamMateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nickname : String, status : Boolean){
            binding.teamMateNickname.text = nickname

            // 팀원이 목표를 인증한 경우
            if (status)
                binding.teamMateStatus.background = ContextCompat.getDrawable(itemView.context, R.drawable.icon_checked)
        }
    }
}
