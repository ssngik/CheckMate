package kr.co.company.capstone.history.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.company.capstone.databinding.ItemCrewBinding

class CrewAdapter(private val nicknames: List<String>) : RecyclerView.Adapter<CrewAdapter.CrewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val binding = ItemCrewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        holder.bind(nicknames[position])
    }

    override fun getItemCount(): Int = nicknames.size

    inner class CrewViewHolder(private val binding: ItemCrewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nickname: String) {
            binding.nicknameTextView.text = nickname
        }
    }
}