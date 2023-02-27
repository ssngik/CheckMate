package kr.co.company.capstone.util.adapter;

import kr.co.company.capstone.util.model.HistorySubItem;
import kr.co.company.capstone.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// 자식 어답터
public class HistorySubItemAdapter extends RecyclerView.Adapter<HistorySubItemAdapter.SubItemViewHolder> {

    private final List<HistorySubItem> historySubItemList;

    HistorySubItemAdapter(List<HistorySubItem> historySubItemList) {
        this.historySubItemList = historySubItemList;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_sub_item, viewGroup, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int i) {
        HistorySubItem historySubItem = historySubItemList.get(i);
        subItemViewHolder.subItemCrew.setText(historySubItem.getSubItemTitle());
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
        return historySubItemList.size();
    }

    static class SubItemViewHolder extends RecyclerView.ViewHolder {
        TextView subItemCrew;

        SubItemViewHolder(View itemView) {
            super(itemView);
            subItemCrew = itemView.findViewById(R.id.sub_item_crew);

        }
    }
}