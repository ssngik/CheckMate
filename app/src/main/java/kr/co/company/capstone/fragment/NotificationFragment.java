package kr.co.company.capstone.fragment;

// Library import
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// AndroidX Library
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// JAVA Library
import java.util.Collections;
import java.util.List;

import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.notification.NotificationInfoResponse;
import kr.co.company.capstone.util.adapter.AlarmAdapter;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.notification.NotificationInfoListResponse;
import kr.co.company.capstone.service.NotificationService;

// Retrofit Library
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.jetbrains.annotations.NotNull;

public class NotificationFragment extends Fragment {

    private final String LOG_TAG = "AlarmPage";
    private NotificationInfoListResponse<NotificationInfoResponse> notificationListResponse;
    private Long lastCursor = null;
    private AlarmAdapter adapter = new AlarmAdapter(Collections.emptyList(), getContext());
    private RecyclerView notificationRecycler;

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

        // 최초 알림 초기화
        if(lastCursor==null){
            getNotifications(notificationRecycler, lastCursor);
        }

        // 스크롤 리스너
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= totalItemCount - 1 && notificationListResponse.isHasNext()) {
                    getNotifications(notificationRecycler, lastCursor);
                }
            }
        };

        notificationRecycler.addOnScrollListener(onScrollListener);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    // 알림 초기화
    private void getNotifications(RecyclerView alarmRecycler, Long cursorId) {
        NotificationService.getService().notifications(cursorId, 10)
                .enqueue(new Callback<NotificationInfoListResponse<NotificationInfoResponse>>() {
                    @Override
                    public void onResponse(Call<NotificationInfoListResponse<NotificationInfoResponse>> call, Response<NotificationInfoListResponse<NotificationInfoResponse>> response) {
                        if (response.isSuccessful()) {

                            notificationListResponse = response.body();
                            List<NotificationInfoResponse> notifications = notificationListResponse.getNotifications();

                            // 최초 호출 시 lastCursor -> null
                            // 추가로 받아올 정보가 있을 시 -> isUpdate -> true
                            boolean isUpdate = lastCursor != null;

                            // 추가로 받아올 정보가 있을 시
                            if(isUpdate && notifications!=null)
                            {
                                alarmRecycler.getRecycledViewPool().clear(); // RecyclerView Pool 초기화
                                adapter.addAll(notifications);
                            }
                            else // 최초 호출 시
                            {
                                adapter=new AlarmAdapter(notifications, getContext());
                                alarmRecycler.setAdapter(adapter);
                            }

                            // Set lastCursor
                            if(notifications.size()>0){
                                int size = notifications.size(); // 알림 개수
                                lastCursor = notifications.get(size - 1).getNotificationId(); // 마지막 알림 위치
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

}