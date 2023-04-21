package kr.co.company.capstone.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.NotificationInfoResponse;
import kr.co.company.capstone.util.adapter.AlarmAdapter;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.NotificationInfoListResponse;
import kr.co.company.capstone.service.NotificationService;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    private String LOG_TAG = "AlarmPage";
    private NotificationInfoListResponse<NotificationInfoResponse> result;
    private Long lastCursor = -1L;
    private AlarmAdapter adapter = new AlarmAdapter(Collections.emptyList(), getContext());
    private RecyclerView notificationRecycler;
    private Long cursorId=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_icon, container, false);

        notificationRecycler = view.findViewById(R.id.alarm_recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1 && result.isHasNext()) {
                    getNotifications(notificationRecycler, lastCursor);
                }
            }
        };

        notificationRecycler.addOnScrollListener(onScrollListener);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d(LOG_TAG, "getInitialNotifications !");
        getInitialNotifications(notificationRecycler, cursorId);

    }

    // 알림 초기화
    private void getInitialNotifications(RecyclerView alarmRecycler, Long cursorId) {
        NotificationService.getService().notifications(cursorId, 10)
                .enqueue(new Callback<NotificationInfoListResponse<NotificationInfoResponse>>() {
                    @Override
                    public void onResponse(Call<NotificationInfoListResponse<NotificationInfoResponse>> call, Response<NotificationInfoListResponse<NotificationInfoResponse>> response) {
                        if (response.isSuccessful()) {
                            NotificationInfoListResponse<NotificationInfoResponse> notificationListResponse = response.body();

                            if(notificationListResponse!=null){
                                List<NotificationInfoResponse> notifications = notificationListResponse.getNotifications();
                                adapter = new AlarmAdapter(notifications, getContext());
                                alarmRecycler.getRecycledViewPool().clear(); // RecyclerView Pool 초기화
                                alarmRecycler.setAdapter(adapter);

                                // lastCursor 설정
                                if(notifications.size()>0){
                                    int size = notifications.size(); // 알림 개수
                                    lastCursor = notifications.get(size - 1).getNotificationId(); // 마지막 알림 위치
                                }
                            }

                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                            Log.d(LOG_TAG, ErrorMessage.getErrorByResponse(response).toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationInfoListResponse<NotificationInfoResponse>> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                        Log.d(LOG_TAG,t.getMessage());
                    }
                });
    }

    private void getNotifications(RecyclerView alarmRecycler, Long cursorId) {
        NotificationService.getService().notifications(cursorId, 10)
                .enqueue(new Callback<NotificationInfoListResponse<NotificationInfoResponse>>() {
                    @Override
                    public void onResponse(Call<NotificationInfoListResponse<NotificationInfoResponse>> call, Response<NotificationInfoListResponse<NotificationInfoResponse>> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, "onResponse: ConfigurationListener::" + call.request().url());
                            result = response.body();
                            Log.d(LOG_TAG, result + " !!!!!!!!!!!!!!!!!!!!!!!!!!!!@!@!@!");
                            alarmRecycler.getRecycledViewPool().clear();
                            result.getNotifications().forEach(adapter::add);
                            lastCursor = result.getNotifications().get(result.getNotifications().size() - 1).getNotificationId();
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                            Log.d(LOG_TAG, "send error");
                            Log.d(LOG_TAG, String.valueOf(response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<NotificationInfoListResponse<NotificationInfoResponse>> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                        Log.d(LOG_TAG, "onFailure");
                        Log.d(LOG_TAG, t.getMessage());
                    }
                });
    }
}