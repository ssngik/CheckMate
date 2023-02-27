package kr.co.company.capstone.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.company.capstone.R;
import kr.co.company.capstone.activity.MainActivity;
import kr.co.company.capstone.fragment.GoalDetailFragment;
import kr.co.company.capstone.fragment.InviteResponseFragment;
import kr.co.company.capstone.fragment.OnErrorFragment;
import kr.co.company.capstone.fragment.TimeLineFragment;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.NotificationInfoResponse;
import kr.co.company.capstone.dto.NotificationDetailResponse;
import kr.co.company.capstone.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
@AllArgsConstructor
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{
    private List<NotificationInfoResponse> alarmList;
    private Context context;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String LOG_TAG = "AlarmAdapter";

    @NonNull
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint({"RecyclerView", "CheckResult"})
    public void onBindViewHolder(@NonNull AlarmAdapter.ViewHolder holder, int position) {
        holder.onBind(alarmList.get(position));
        if(alarmList.get(position).isChecked()) holder.verticalView.setBackgroundColor(Color.LTGRAY);

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationInfoResponse info = alarmList.get(position);
                if (Objects.equals(info.getType(), "INVITE_GOAL") && !info.isChecked()) {
                    callNotificationFindApi(info, view);
                }
                else if (!Objects.equals(info.getType(), "INVITE_GOAL")) {
                    info.setChecked(true);
                    holder.verticalView.setBackgroundColor(Color.LTGRAY);
                    callNotificationFindApi(info, view);
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void callNotificationFindApi(NotificationInfoResponse info, View view) {
        NotificationService.getService().findNotification(info.getNotificationId())
                .enqueue(new Callback<NotificationDetailResponse>() {
                    @SneakyThrows
                    @Override
                    public void onResponse(Call<NotificationDetailResponse> call, Response<NotificationDetailResponse> response) {
                        if (response.isSuccessful()) {
                            NotificationDetailResponse notificationResponse = response.body();
                            Map<String, String> attributes = objectMapper.readValue(notificationResponse.getAttributes(), Map.class);
                            Log.d(LOG_TAG, "attributes : " + notificationResponse.getAttributes());
                            Bundle bundle = new Bundle();
                            Intent nextPage = new Intent(context.getApplicationContext(), MainActivity.class);

                            switch (notificationResponse.getNotificationType()) {
                                case "INVITE_GOAL":
                                    InviteResponseFragment inviteResponseFragment = new InviteResponseFragment();
                                    bundle.putString("messageBody", info.getBody());
                                    bundle.putLong("teamMateId", Long.parseLong(String.valueOf(attributes.get("teamMateId"))));
                                    bundle.putLong("notificationId", info.getNotificationId());
                                    inviteResponseFragment.setArguments(bundle);
                                    Log.d(LOG_TAG, "invite_goal");
                                    Navigation.findNavController(view).navigate(R.id.action_navigation_alarm_to_inviteResponseFragment, bundle);
                                    break;
                                case "POST_UPLOAD": // 목표 수행 인증
                                    TimeLineFragment timeLineFragment = new TimeLineFragment();
                                    bundle.putString("date", info.getSendAt().replace("-", "").split("[T.]")[0]);
                                    bundle.putLong("goalId", Long.parseLong(String.valueOf(attributes.get("goalId"))));
                                    timeLineFragment.setArguments(bundle);
                                    Log.d(LOG_TAG, "invite_upload");
                                    Navigation.findNavController(view).navigate(R.id.action_navigation_alarm_to_timeLineFragment, bundle);
                                    break;
                                case "INVITE_REPLY":
                                    GoalDetailFragment goalDetailFragment = new GoalDetailFragment();
                                    bundle.putLong("goalId", Long.parseLong(String.valueOf(attributes.get("goalId"))));
                                    goalDetailFragment.setArguments(bundle);
                                    Log.d(LOG_TAG, "invite_reply");
                                    Log.d(LOG_TAG, "attribute" + attributes);
                                    if(attributes.get("accept").equals("true")){ // 수락한 알람
                                        Navigation.findNavController(view).navigate(R.id.action_navigation_alarm_to_goalDetailFragment, bundle);
                                    }else{
                                        Log.d(LOG_TAG, "false"); // 거절한 알람
                                        Toast.makeText(context, "다음에 같이해요! :)",Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                default:
                                    Log.d(LOG_TAG, "default"); // 목표 수행 완수, 목표 퇴출
                                    nextPage.putExtras(bundle);
                                    context.startActivity(nextPage);
                                    break;
                            }
                        }
                        else{
                            Log.d(LOG_TAG, String.valueOf(response.code()));
                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<NotificationDetailResponse> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(onErrorFragment.getChildFragmentManager(), "error");
                    }
                });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(NotificationInfoResponse notificationInfoResponse) {
        this.alarmList.add(notificationInfoResponse);
        notifyItemInserted(alarmList.size()-1);
    }

    public void setAlarmList(ArrayList<NotificationInfoResponse> list){
        this.alarmList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView alarmDate;
        RelativeLayout body;
        View verticalView;

        public ViewHolder(@NonNull View view) {
            super(view);
            verticalView = view.findViewById(R.id.vertical_view);
            body = view.findViewById(R.id.alarm_body);
            title = view.findViewById(R.id.alarm_title);
            content = view.findViewById(R.id.alarm_content);
            alarmDate = view.findViewById(R.id.alarm_date);
        }

        void onBind(NotificationInfoResponse item) {
            title.setText(item.getTitle());
            content.setText(item.getBody());
            alarmDate.setText(item.getSendAt().replace("-", "").split("[T.]")[1]);
        }
    }
}
