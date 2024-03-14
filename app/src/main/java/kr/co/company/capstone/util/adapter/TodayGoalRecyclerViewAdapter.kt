import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.TodayGoalItemviewBinding
import kr.co.company.capstone.dto.goal.TodayGoalInfoResponse

class TodayGoalRecyclerViewAdapter(val todayTodoList : List<TodayGoalInfoResponse>) : RecyclerView.Adapter<TodayGoalRecyclerViewAdapter.Holder>(){

    interface ItemClick{ fun onClick(view : View, position: Int, goalId: Long) }
    var itemClick : ItemClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayGoalRecyclerViewAdapter.Holder {
        val binding = TodayGoalItemviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: TodayGoalRecyclerViewAdapter.Holder, position: Int) {
        holder.itemView.setOnClickListener { itemClick?.onClick(it, position, todayTodoList[position].goalId) }
        holder.todayTodoItem.text = todayTodoList[position].title

        if (todayTodoList[position].checked)
            holder.isCompletedImg.setImageResource(R.drawable.recyclerview_item_checked) // 이미 완료한 목표인 경우
        else
            holder.isCompletedImg.setImageResource(R.drawable.recyclerview_item_unchecked) // 완료하지 않은 목표인 경우
    }

    override fun getItemCount(): Int {
        return todayTodoList.size
    }

    inner class Holder(val binding: TodayGoalItemviewBinding) : RecyclerView.ViewHolder(binding.root){
        val todayTodoItem = binding.todayTodoTextview
        val isCompletedImg = binding.isComplete
    }
}