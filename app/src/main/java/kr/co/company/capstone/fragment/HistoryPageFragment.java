package kr.co.company.capstone.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import kr.co.company.capstone.service.GoalInquiryService;
import kr.co.company.capstone.util.adapter.HistoryAdapter;
import kr.co.company.capstone.util.model.HistoryItem;
import kr.co.company.capstone.util.model.HistorySubItem;
import kr.co.company.capstone.R;
import kr.co.company.capstone.util.SharedPreferenceUtil;
import kr.co.company.capstone.dto.ErrorMessage;
import kr.co.company.capstone.dto.goal.*;
import kr.co.company.capstone.service.UserService;
import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@NoArgsConstructor
public class HistoryPageFragment extends Fragment {
    private static final String LOG_TAG = "HistoryPageFragment";
    private String goalId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Long userId = SharedPreferenceUtil.getLong(getContext(), "userId");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_icon, container, false);

        RecyclerView HistoryRecyclerView = view.findViewById(R.id.history_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        HistoryRecyclerView.setLayoutManager(layoutManager);

        GoalInquiryService.getService().userHistory()
                .enqueue(new Callback<GoalInfoListResponse<HistoryGoalInfoResponse>>() {
                    @Override
                    public void onResponse(Call<GoalInfoListResponse<HistoryGoalInfoResponse>> call, Response<GoalInfoListResponse<HistoryGoalInfoResponse>> response) {
                        if (response.isSuccessful()) {
                            GoalInfoListResponse<HistoryGoalInfoResponse> result = response.body();
                            Log.d(LOG_TAG, result.toString());
                            List<HistoryItem> historyItems = result.getGoals()
                                    .stream()
                                    .map(g -> {
                                        List<HistorySubItem> historySubItems = g.getMateNicknames().stream().map(tm -> new HistorySubItem(tm, "null")).collect(Collectors.toList());
                                        return new HistoryItem(g.getGoalId(), g.getTitle(), g.getStartDate(), g.getEndDate(), g.getAchievementRate(), historySubItems);
                                    })
                                    .collect(Collectors.toList());

                            HistoryAdapter historyAdapter = new HistoryAdapter(historyItems);
                            historyAdapter.setOnItemClickListener(new HistoryAdapter.OnHistoryItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    TimeLineFragment timeLineFragment = new TimeLineFragment();
                                    Bundle goalIdData = new Bundle();
                                    goalIdData.putLong("goalId", historyItems.get(position).getGoalId());
                                    goalIdData.putString("date", historyItems.get(position).getStartDate());
                                    Log.d(LOG_TAG, "date : " + historyItems.get(position).getStartDate());
                                    timeLineFragment.setArguments(goalIdData);
                                    Navigation.findNavController(view).navigate(R.id.action_navigation_history_to_timeLineFragment, goalIdData);
                                }
                            });
                            HistoryRecyclerView.setAdapter(historyAdapter);
                        }else{
                            showErrorDialog("정보를 불러올 수 없습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<GoalInfoListResponse<HistoryGoalInfoResponse>> call, Throwable t) {
                        showErrorDialog("문제가 발생했습니다.");
                    }
                });


        return view;
    }
    private void showErrorDialog(String errorMessage){
        ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.Companion.getErrorMessage(errorMessage);
        errorDialogFragment.show(getParentFragmentManager(), "ErrorDialogFragment");
    }
}