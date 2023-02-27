package kr.co.company.capstone.util.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.goal.GoalDate;
import kr.co.company.capstone.service.DayConverter;


public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter{
    private static final String LOG_TAG = "RecyclerViewAdapter";

    List<GoalDate> dates;
    Context context;

    public CalendarRecyclerViewAdapter(Context context, List<GoalDate> dates) {
        this.dates = dates;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_calendar_date, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder)holder;
        String date = dates.get(position).getLocalDate();
        String[] split = date.split("-");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String weekDay = DayConverter.convertEngToKor(LocalDate.parse(date, format).getDayOfWeek().toString());

        myViewHolder.goalDate.setText("  "+weekDay + "\n  " + split[2] +"일");
        myViewHolder.goalDate.setTextColor(dates.get(position).isWorkingDay()? dates.get(position).isChecked()?
                Color.parseColor("#1B5E20") : LocalDate.parse(date, format).isBefore(LocalDate.now())? Color.parseColor("#D32F2F") : Color.parseColor("#4CAF50") : Color.GRAY);

        myViewHolder.dateIcon.setImageResource(!dates.get(position).isWorkingDay() ? 0 :
                dates.get(position).isChecked() ? R.drawable.attendance_icon :
                        LocalDate.parse(date, format).isBefore(LocalDate.now()) ? R.drawable.absent_icon : 0);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView goalDate;
        ImageView dateIcon;

        public MyViewHolder(@NonNull View view) {
            super(view);
            goalDate =  view.findViewById(R.id.calendar_date);
            dateIcon = view.findViewById(R.id.calendar_icon);
        }
    }

}
