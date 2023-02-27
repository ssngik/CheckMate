package kr.co.company.capstone.util.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.company.capstone.util.model.HistoryItem;
import kr.co.company.capstone.R;
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<HistoryItem> itemList;
    private String LOG_TAG = "HistoryAdapter";

    private OnHistoryItemClickListener historyItemClickListener;

    public interface OnHistoryItemClickListener{
        void onItemClick(View v, int pos);
    }

    public void setOnItemClickListener(OnHistoryItemClickListener listener){
        this.historyItemClickListener = listener;
    }

    public HistoryAdapter(List<HistoryItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        HistoryItem item = itemList.get(i);
        itemViewHolder.historyTitle.setText(item.getItemTitle());
        itemViewHolder.historyDate.setText(item.getStartDate() + " ~ " + item.getEndDate());
        itemViewHolder.achievementRate.setText(item.getAchievement() + " %");
        itemViewHolder.achievement.setProgress((int)Math.round(item.getAchievement()));

        // 자식 레이아웃 매니저 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                itemViewHolder.subItem.getContext(), 2);

        gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        gridLayoutManager.setInitialPrefetchItemCount(item.getHistorySubItemList().size());

        // 자식 어댑터 설정
        HistorySubItemAdapter historySubItemAdapter = new HistorySubItemAdapter(item.getHistorySubItemList());

        itemViewHolder.subItem.setLayoutManager(gridLayoutManager);
        itemViewHolder.subItem.setAdapter(historySubItemAdapter);
        itemViewHolder.subItem.setRecycledViewPool(viewPool);

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView historyTitle, historyDate;

        private RecyclerView subItem;
        private TextView achievementRate;
        private ProgressBar achievement;

        ItemViewHolder(View itemView) {
            super(itemView);
            // 부모 타이틀
            historyTitle = itemView.findViewById(R.id.history_title);
            historyDate = itemView.findViewById(R.id.history_date);
            achievementRate = itemView.findViewById(R.id.achievement_rate);
            achievement = itemView.findViewById(R.id.achievement);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        if(historyItemClickListener!=null){
                            historyItemClickListener.onItemClick(view, position);
                        }
                    }
                }
            });

            // 자식아이템 영역
            subItem = itemView.findViewById(R.id.history_sub_item);
        }
    }
}
