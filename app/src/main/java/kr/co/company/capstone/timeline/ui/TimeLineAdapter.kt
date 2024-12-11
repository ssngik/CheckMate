package kr.co.company.capstone.timeline.ui

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kr.co.company.capstone.R
import kr.co.company.capstone.databinding.ItemTimeLineBinding
import kr.co.company.capstone.dto.timeline.PostItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeLineAdapter(
    private val posts: MutableList<PostItem>,
    private val onLikeClicked: (Long, Boolean) -> Unit,
    private val userId: Long
) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {
    private val expandedPostIds = mutableSetOf<Long>()
    inner class TimeLineViewHolder(val binding: ItemTimeLineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostItem) {
            bindUserInfo(post)   // 작성자 정보 및 업로드 시간 표시
            bindContent(post)    // 게시글 내용 및 확장/축소 처리
            bindLikeInfo(post)   // 좋아요 상태 및 좋아요 수 표시
            bindImages(post)     // 이미지 ViewPagerr 설정
        }

        private fun bindUserInfo(post: PostItem) {
            binding.uploaderNickname.text = post.uploaderNickname
            binding.uploadTime.text = formatUploadTime(post.uploadAt)
        }

        private fun bindContent(post: PostItem) {
            binding.postText.text = post.content
            binding.postText.post {
                val lineCount = binding.postText.lineCount
                // 4줄 이상일 때  Expand/Collapse
                if (lineCount >= 4) {
                    binding.postText.setOnClickListener {
                        toggleContentExpanded(post)
                    }
                } else {
                    binding.postText.setOnClickListener(null)
                }
            }
        }

        private fun toggleContentExpanded(post: PostItem) {
            if (expandedPostIds.contains(post.postId)) {
                expandedPostIds.remove(post.postId)
                binding.postText.maxLines = 4
                binding.postText.ellipsize = TextUtils.TruncateAt.END
            } else {
                expandedPostIds.add(post.postId)
                binding.postText.maxLines = Int.MAX_VALUE
                binding.postText.ellipsize = null
            }
        }

        private fun bindLikeInfo(post: PostItem) {
            val isLiked = post.likedUserIds.contains(userId)
            binding.likeButton.setImageResource(if (isLiked) R.drawable.like_on else R.drawable.like_off)
            binding.likePeople.text = "${post.likedUserIds.size}명이 좋아요를 눌렀어요"
            binding.likeButton.setOnClickListener { onLikeClicked(post.postId, isLiked) }
        }

        private fun bindImages(post: PostItem) {
            val imageAdapter = ImagePagerAdapter(post.imageUrls)
            binding.postImage.apply {
                adapter = imageAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
        }

        private fun formatUploadTime(uploadAt: String): String {
            return try {
                val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
                val formatterOutput = DateTimeFormatter.ofPattern("yyyy.MM.dd, HH:mm")
                val dateTime = LocalDateTime.parse(uploadAt, formatterInput)
                dateTime.format(formatterOutput)
            } catch (e: Exception) {
                uploadAt // 파싱 실패 시 원본 그대로
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val binding = ItemTimeLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        holder.bind(posts[position])
    }



    fun addPosts(newPosts: List<PostItem>) {
        val positionStart = posts.size
        posts.addAll(newPosts)
        notifyItemRangeInserted(positionStart, newPosts.size)
    }

    fun clearPosts() {
        posts.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int { return posts.size }

    fun getPosts(): List<PostItem> { return posts }

}