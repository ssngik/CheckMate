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

import kr.co.company.capstone.util.adapter.AlarmAdapter;
import kr.co.company.capstone.R;
import kr.co.company.capstone.dto.NotificationInfoListResponse;
import kr.co.company.capstone.service.NotificationService;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlarmPageFragment extends Fragment {

    private String LOG_TAG = "AlarmPage";
    private NotificationInfoListResponse result;
    private Long lastCursor = -1L;
    private AlarmAdapter adapter = new AlarmAdapter(Collections.emptyList(), getContext());
    private RecyclerView alarmRecycler;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_icon, container, false);


        alarmRecycler = view.findViewById(R.id.alarm_recyclerView);

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
                    getNotifications(alarmRecycler, lastCursor);
                }
            }
        };

        alarmRecycler.addOnScrollListener(onScrollListener);
        alarmRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        getInitialNotifications(alarmRecycler, null);




    }

    private void getInitialNotifications(RecyclerView alarmRecycler, Long cursorId) {
        NotificationService.getService().notifications(cursorId)
                .enqueue(new Callback<NotificationInfoListResponse>() {
                    @Override
                    public void onResponse(Call<NotificationInfoListResponse> call, Response<NotificationInfoListResponse> response) {
                        if (response.isSuccessful()) {
                            result = response.body();
                            adapter = new AlarmAdapter(result.getValues(), getContext());
                            alarmRecycler.setAdapter(adapter);
                            int size = result.getValues().size();
                            if (size > 0)
                                lastCursor = result.getValues().get(size - 1).getNotificationId();
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
//                            Log.d(LOG_TAG, "send error");
//                            Log.d(LOG_TAG, String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationInfoListResponse> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                    }
                });
    }
    private void getNotifications(RecyclerView alarmRecycler, Long cursorId) {
        NotificationService.getService().notifications(cursorId)
                .enqueue(new Callback<NotificationInfoListResponse>() {
                    @Override
                    public void onResponse(Call<NotificationInfoListResponse> call, Response<NotificationInfoListResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, "onResponse: ConfigurationListener::" + call.request().url());
                            result = response.body();
                            alarmRecycler.getRecycledViewPool().clear();
                            result.getValues().forEach(adapter::add);
                            lastCursor = result.getValues().get(result.getValues().size() - 1).getNotificationId();
                        } else {
                            OnErrorFragment onErrorFragment = new OnErrorFragment();
                            onErrorFragment.show(getChildFragmentManager(), "error");
                            Log.d(LOG_TAG, "send error");
                            Log.d(LOG_TAG, String.valueOf(response.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<NotificationInfoListResponse> call, Throwable t) {
                        OnErrorFragment onErrorFragment = new OnErrorFragment();
                        onErrorFragment.show(getChildFragmentManager(), "error");
                        Log.d(LOG_TAG, "onFailure");
                        Log.d(LOG_TAG, t.getMessage());
                    }
                });
    }
}