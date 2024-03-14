import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.company.capstone.databinding.OngoingGoalItemviewBinding
import kr.co.company.capstone.dto.goal.OngoingGoalInfoResponse

class OngoingGoalRecyclerViewAdapter(val ongoingTodoList : List<OngoingGoalInfoResponse>) : RecyclerView.Adapter<OngoingGoalRecyclerViewAdapter.Holder>(){
    interface ItemClick{ fun onClick(view : View, position: Int, goalId: Long) }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): OngoingGoalRecyclerViewAdapter.Holder {
        val binding = OngoingGoalItemviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: OngoingGoalRecyclerViewAdapter.Holder, position: Int) {
        holder.itemView.setOnClickListener { itemClick?.onClick(it, position, ongoingTodoList[position].goalId) }
        holder.ongoingTodoItem.text = ongoingTodoList[position].title
    }

    override fun getItemCount(): Int {
        return ongoingTodoList.size
    }

    inner class Holder(val binding: OngoingGoalItemviewBinding) : RecyclerView.ViewHolder(binding.root){
        val ongoingTodoItem = binding.ongoingTodoTextview
    }
}