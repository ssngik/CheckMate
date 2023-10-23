package kr.co.company.capstone.util.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.goal.GoalInfo;
import kr.co.company.capstone.fragment.GoalDetailFragment;


public class TodayGoalRecyclerViewAdapter extends RecyclerView.Adapter{
    private static final String LOG_TAG = "RecyclerViewAdapter";
    GoalDetailFragment goalDetailFragment;
    List<? extends GoalInfo> goals;

    Context context;

    public TodayGoalRecyclerViewAdapter(Context context, List<? extends GoalInfo> goals) {
        this.goals = goals;
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return goals.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_goal_itemview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.todayTodo.setText(goals.get(position).getTitle());
        if(goals.get(position).isChecked()){
            myViewHolder.imageView.setImageResource(R.drawable.complete);
            myViewHolder.todayTodo.setTextColor(Color.RED);
            myViewHolder.todayTodo.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        myViewHolder.todayTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalDetailFragment = new GoalDetailFragment();
                Bundle goalIdRc = new Bundle();
                long goalId = goals.get(myViewHolder.getAdapterPosition()).getGoalId();
                goalIdRc.putLong("goalId",goalId);

                goalDetailFragment.setArguments(goalIdRc);
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_goalDetailFragment, goalIdRc);

            }
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView todayTodo;
        ImageView imageView;
        public MyViewHolder(@NonNull View todayGoalItemView) {
            super(todayGoalItemView);
            todayTodo =  todayGoalItemView.findViewById(R.id.today_todo_textview);
            imageView = todayGoalItemView.findViewById(R.id.imageview);
        }
    }


}
