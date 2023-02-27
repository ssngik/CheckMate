package kr.co.company.capstone.util.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.company.capstone.dto.post.PostItem;
import kr.co.company.capstone.service.LikeService;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import kr.co.company.capstone.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<PostItem> posts;
    private Context context;
    private Integer minimumLike;
    private static final String LOG_TAG = "PostsAdapter";

    public PostsAdapter(ArrayList<PostItem> posts, Context context, Integer minimumLike) {
        this.posts = posts;
        this.context = context;
        this.minimumLike = minimumLike;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        PostItem postItem = posts.get(position);
        final PostViewHolder viewHolder = (PostViewHolder) holder;
        Log.d(LOG_TAG, viewHolder.toString());
        viewHolder.username.setText(posts.get(position).getUsername());
        viewHolder.text.setText(posts.get(position).getText());
        viewHolder.uploadAt.setText(posts.get(position).getUploadAt().split("[T.]")[1]);
        viewHolder.like.setText(getLikeCount(posts.get(position).getLikeCount()));
        if(LocalDate.now().minusDays(1L).isAfter(getUplodAtLocalDate(position))) viewHolder.likeButton.setEnabled(false);
        viewHolder.likeButton.setImageResource(posts.get(position).isLiked() ? R.drawable.like : R.drawable.before_like);
        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!posts.get(viewHolder.getAdapterPosition()).isLiked()) {
                    like(viewHolder.getAdapterPosition());
                    posts.get(viewHolder.getAdapterPosition()).setLiked(true);
                    posts.get(viewHolder.getAdapterPosition()).setLikeCount(posts.get(viewHolder.getAdapterPosition()).getLikeCount() + 1);
                    viewHolder.likeButton.setImageResource(R.drawable.like);
                }
                else {
                    unlike(viewHolder.getAdapterPosition());
                    viewHolder.likeButton.setImageResource(R.drawable.before_like);
                    posts.get(viewHolder.getAdapterPosition()).setLikeCount(posts.get(viewHolder.getAdapterPosition()).getLikeCount() - 1);
                    posts.get(viewHolder.getAdapterPosition()).setLiked(false);
                }
                viewHolder.like.setText(getLikeCount(posts.get(viewHolder.getAdapterPosition()).getLikeCount()));
            }
        });
        //Glide.with(context).load(posts.get(position).getPhoto()).into(viewHolder.photo);

        // 자식 레이아웃 매니저 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                viewHolder.photo.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        layoutManager.setInitialPrefetchItemCount(postItem.getPostSubItemList().size());


        // 자식 어댑터 설정
        PostSubAdapter postSubAdapter = new PostSubAdapter(postItem.getPostSubItemList());

        viewHolder.photo.setLayoutManager(layoutManager);
        viewHolder.photo.setAdapter(postSubAdapter);
        viewHolder.photo.setRecycledViewPool(viewPool);
    }

    @NotNull
    private String getLikeCount(int likeCount) {
        return likeCount + " Like";
    }

    @SuppressLint("NewApi")
    private LocalDate getUplodAtLocalDate(int position) {
        String uploadedDate = posts.get(position).getUploadAt().replace("-", "").split("[T.]")[0];
        return LocalDate.parse(uploadedDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private void like(int position) {
        LikeService.getService().like(posts.get(position).getPostId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Log.d(LOG_TAG, "like success");
                        }
                        else Log.d(LOG_TAG, "like fail");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(LOG_TAG, t.getMessage());
                    }
                });
    }

    private void unlike(int position) {
        LikeService.getService().unlike(posts.get(position).getPostId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Log.d(LOG_TAG, "unlike success");
                        }
                        else Log.d(LOG_TAG, "unlike fail");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d(LOG_TAG, t.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, text, like, uploadAt;
        ImageButton likeButton;
        private RecyclerView photo;

        public PostViewHolder(@NonNull View view) {
            super(view);
            username = view.findViewById(R.id.username);
            text = view.findViewById(R.id.before_nickname);
            like = view.findViewById(R.id.like);
            uploadAt = view.findViewById(R.id.time);
            likeButton = view.findViewById(R.id.imageButton);
            photo = view.findViewById(R.id.photo);
        }
    }
}