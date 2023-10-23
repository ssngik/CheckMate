package kr.co.company.capstone.util.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.goal.OngoingGoalInfo;
import kr.co.company.capstone.fragment.GoalDetailFragment;


public class OngoingGoalRecyclerViewAdapter extends RecyclerView.Adapter{
    private static final String LOG_TAG = "RecyclerViewAdapter";

    List <? extends OngoingGoalInfo> goals;
    Context context;

    public OngoingGoalRecyclerViewAdapter(Context context, List<? extends OngoingGoalInfo> goals) {
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
                .inflate(R.layout.ongoing_goal_itemview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.ongoingTodo.setText(goals.get(position).getTitle());
        myViewHolder.ongoingTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoalDetailFragment goalDetailFragment = new GoalDetailFragment();
                Bundle goalIdRc = new Bundle();
                long goalId = goals.get(myViewHolder.getAdapterPosition()).getGoalId();

                goalIdRc.putLong("goalId",goalId);
                goalDetailFragment.setArguments(goalIdRc);
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_goalDetailFragment, goalIdRc);

            }
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ongoingTodo;
        public MyViewHolder(@NonNull View ongoingItemView) {
            super(ongoingItemView);
            ongoingTodo =  itemView.findViewById(R.id.ongoing_todo_textview);
        }
    }


}
