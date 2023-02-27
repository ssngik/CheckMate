package kr.co.company.capstone.util.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.team_mate.TeamMateResponse;


public class TeamMateRecyclerViewAdapter extends RecyclerView.Adapter{
    private static final String LOG_TAG = "RecyclerViewAdapter";

    List<TeamMateResponse> teamMates;
    Context context;

    public TeamMateRecyclerViewAdapter(Context context, List<TeamMateResponse> teamMates) {
        this.teamMates = teamMates;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return teamMates.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(LOG_TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_mate_view , parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.teamMateNickname.setText(teamMates.get(position).getNickname());
        myViewHolder.teamMateStatus.setText(teamMates.get(position).isUploaded()?"O":"X");
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView teamMateStatus;
        TextView teamMateNickname;

        public MyViewHolder(@NonNull View view) {
            super(view);
            teamMateNickname =  view.findViewById(R.id.team_mate_nickname);
            teamMateStatus = view.findViewById(R.id.team_mate_status);
        }
    }

}
