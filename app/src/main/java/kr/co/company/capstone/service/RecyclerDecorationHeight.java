package kr.co.company.capstone.service;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerDecorationHeight extends RecyclerView.ItemDecoration {
    private final int divHeight;


    public RecyclerDecorationHeight(int divHeight) {
        this.divHeight = divHeight;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1){
            outRect.bottom = divHeight;
        }
    }

}