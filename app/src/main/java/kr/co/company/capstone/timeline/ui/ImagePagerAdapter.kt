package kr.co.company.capstone.timeline.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.company.capstone.databinding.PostImageItemBinding

class ImagePagerAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    // ViewHolder 정의
    inner class ImageViewHolder(private val binding: PostImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            val cleanUrl = imageUrl.replace(Regex("[\\p{C}\\p{Z}]"), "").trim()
            Glide.with(binding.postImage.context)
                .load(cleanUrl)
                .into(binding.postImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = PostImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }


    override fun getItemCount(): Int = imageUrls.size
}
