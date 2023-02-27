package kr.co.company.capstone.util.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.company.capstone.R;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.MyViewHolder> {
    private Context context;
    //private String[] sliderImage;
    private TypedArray mItems; //drawable 경로 데이터 저장 배열


    public ViewPagerAdapter(Context context, TypedArray mItems) {
        this.context = context;
        this.mItems = mItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int index = position % mItems.length();
        if(index >= mItems.length())
        {
            index = 0;
        }
        holder.mImageView.setImageResource(mItems.getResourceId(index, -1));
    }

    // 무한 스크롤을 위해 MAX 값 반영 x 보류
    @Override
    public int getItemCount() {
        //return Integer.MAX_VALUE;
        return mItems.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }
    }
}