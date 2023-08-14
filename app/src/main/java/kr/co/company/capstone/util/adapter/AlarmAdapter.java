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
import kr.co.company.capstone.dto.notification.NotificationInfoResponse;
import kr.co.company.capstone.dto.notification.NotificationDetailResponse;
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
        NotificationInfoResponse info = alarmList.get(position);

        holder.onBind(alarmList.get(position));
        if(alarmList.get(position).isChecked()) holder.checkPoint.setBackgroundColor(Color.LTGRAY);
        // 확인한 경우 -> checked -> true

        holder.body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNotificationFindApi(info, view);

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

                            Bundle bundle = new Bundle();
                            Intent nextPage = new Intent(context.getApplicationContext(), MainActivity.class);

                            handleNotificationType(notificationResponse, attributes, bundle, nextPage, info, view);
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

    // 알람 종류별 분류
    private void handleNotificationType(NotificationDetailResponse notificationResponse, Map<String, String> attributes, Bundle bundle, Intent nextPage, NotificationInfoResponse info, View view) {
        switch (notificationResponse.getType()) {
            case "INVITE_GOAL": // 초대 알람

                // 초대 응답 페이지로 이동
                goToInviteResponse(bundle, info, view);

                break;
            case "POST_UPLOAD": // 목표 수행 인증

                // 타임라인으로 이동
                goToTimeLine(attributes, bundle, info, view);

                break;
            case "INVITE_REPLY": // 초대 응답 알람

                // 목표 상세 화면으로 이동
                goToGoalDetail(attributes, bundle, view);

                break;
            default:
                nextPage.putExtras(bundle);
                context.startActivity(nextPage);
                break;
        }
    }

    private void goToGoalDetail(Map<String, String> attributes, Bundle bundle, View view) {
        GoalDetailFragment goalDetailFragment = new GoalDetailFragment();

        bundle.putLong("goalId", Long.parseLong(String.valueOf(attributes.get("goalId"))));
        goalDetailFragment.setArguments(bundle);

        if(attributes.get("accept").equals("true")){ // 수락한 알람
            Navigation.findNavController(view).navigate(R.id.action_navigation_alarm_to_goalDetailFragment, bundle);
        }else{ // 거절한 알람
            Toast.makeText(context, "다음에 같이해요! :)",Toast.LENGTH_LONG).show();
        }
    }

    private void goToTimeLine(Map<String, String> attributes, Bundle bundle, NotificationInfoResponse info, View view) {
        TimeLineFragment timeLineFragment = new TimeLineFragment();

        bundle.putString("date", info.getSendAt().replace("-", "").split("[T.]")[0]);
        bundle.putLong("goalId", Long.parseLong(String.valueOf(attributes.get("goalId"))));
        timeLineFragment.setArguments(bundle);

        Navigation.findNavController(view).navigate(R.id.action_navigation_alarm_to_timeLineFragment, bundle);
    }

    private void goToInviteResponse(Bundle bundle, NotificationInfoResponse info, View view) {
        InviteResponseFragment inviteResponseFragment = new InviteResponseFragment();

        bundle.putString("messageBody", info.getContent());
        bundle.putLong("notificationId", info.getNotificationId());
        inviteResponseFragment.setArguments(bundle);

        Navigation.findNavController(view).navigate(R.id.action_navigation_alarm_to_inviteResponseFragment, bundle);
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

    // 새로운 알림
    public void addAll(List<NotificationInfoResponse> newNotifications) {
        alarmList.addAll(newNotifications);
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
        View checkPoint;

        public ViewHolder(@NonNull View view) {
            super(view);
            checkPoint = view.findViewById(R.id.check_point);
            body = view.findViewById(R.id.alarm_body);
            title = view.findViewById(R.id.alarm_title);
            content = view.findViewById(R.id.alarm_content);
            alarmDate = view.findViewById(R.id.alarm_date);
        }

        void onBind(NotificationInfoResponse item) {
            title.setText(item.getTitle());
            content.setText(item.getContent());
            // 23.09.21 09:06
            alarmDate.setText(item.getSendAt().replace("-", "").split("[T.]")[1]);
//            Log.d(LOG_TAG, alarmDate.toString());
//            Log.d(LOG_TAG, item.toString());
        }
    }
}
