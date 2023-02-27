package kr.co.company.capstone.util.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import kr.co.company.capstone.GlobalApplication;
import kr.co.company.capstone.dto.post.PostSubItem;
import kr.co.company.capstone.R;

import java.util.ArrayList;

public class PostSubAdapter extends RecyclerView.Adapter<PostSubAdapter.PostSubItemViewHolder>{
    private String LOG_TAG = "PostSubAdapter";

    private ArrayList<PostSubItem> postSubItemList;

    PostSubAdapter(ArrayList<PostSubItem> postSubItemList) {
        this.postSubItemList = postSubItemList;
    }

    @NonNull
    @Override
    public PostSubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_post_sub_item, viewGroup, false);
        return new PostSubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostSubItemViewHolder postSubItemViewHolder, int i) {
        PostSubItem postSubItem = postSubItemList.get(i);
        Glide.with(GlobalApplication.getAppContext()).load("https://kr.object.ncloudstorage.com/checkmate/" + postSubItem.getImageUrl())
                .override(850, 600)
                .centerCrop()
                .into(postSubItemViewHolder.imgSubItem);
    }

    @Override
    public int getItemCount() {
        return postSubItemList.size();
    }

    class PostSubItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSubItem;
        PostSubItemViewHolder(View itemView) {
            super(itemView);
            imgSubItem = itemView.findViewById(R.id.img_sub_item);
        }
    }

}
