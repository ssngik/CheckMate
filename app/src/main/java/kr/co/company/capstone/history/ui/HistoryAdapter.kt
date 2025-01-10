package kr.co.company.capstone.history.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.company.capstone.databinding.HistoryItemBinding
import kr.co.company.capstone.dto.history.HistoryGoalInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HistoryAdapter(
    private val goals: MutableList<HistoryGoalInfo>,
    private val onDetailClick: (Long) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int = goals.size

    fun updateGoals(newGoals: List<HistoryGoalInfo>) {
        goals.clear()
        goals.addAll(newGoals)
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(goal: HistoryGoalInfo) {
            binding.historyTitle.text = goal.title

            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val endDate = LocalDate.parse(goal.endDate, formatter)

            if (today.isBefore(endDate) || today.isEqual(endDate)) {
                binding.historyDateValue.text = "${goal.startDate} - 진행중"
            } else {
                binding.historyDateValue.text = "${goal.startDate} - ${goal.endDate}"
            }

            binding.achievementValue.text = "${goal.achievementPercent}%"
            binding.achievementBar.progress = goal.achievementPercent.toInt()

            binding.showHistoryDetail.setOnClickListener {
                onDetailClick(goal.goalId)
            }

            binding.crewRecyclerview.apply {
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
                adapter = CrewAdapter(goal.mateNicknames)
            }

            binding.crewRecyclerview.setOnTouchListener { _, motionEvent ->
                binding.root.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
        }
    }
}
