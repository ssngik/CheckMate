package kr.co.company.capstone.util.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.ViewCalendarDateBinding
import kr.co.company.capstone.dto.goal.GoalCalendar
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarRecyclerViewAdapter(private val calendarList: List<GoalCalendar>) :
    RecyclerView.Adapter<CalendarRecyclerViewAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding= ViewCalendarDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return calendarList.size
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendarDay = calendarList[position]
        val date = calendarList[position].date
        val dayOfWeek = getDayOfWeek(date.dayOfWeek)
        holder.bind(calendarDay, dayOfWeek)

        val currentDate: LocalDate = LocalDate.now()

        // 현재 날짜와 같은 경우 표시
        if (calendarDay.date == currentDate){
            holder.binding.itemDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.checkmate_color))
            holder.binding.itemDayOfTheWeek.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.checkmate_color))
        }

        // 목표 인증일인 경우
        if (calendarDay.isGoalDay) {
            holder.binding.itemDate.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.calendar_checked)
            holder.binding.itemDate.setTextColor(Color.WHITE)
        }
    }

    inner class CalendarViewHolder(val binding: ViewCalendarDateBinding) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(calendarDay: GoalCalendar, dayOfWeek: String){
            binding.itemDayOfTheWeek.text = dayOfWeek
            binding.itemDate.text = calendarDay.date.dayOfMonth.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayOfWeek(dayOfWeek : DayOfWeek) : String {
        return when (dayOfWeek){
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
        }
    }

}
