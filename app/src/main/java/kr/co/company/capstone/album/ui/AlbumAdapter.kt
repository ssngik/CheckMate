package kr.co.company.capstone.album.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.company.capstone.databinding.ItemAlbumBinding

class AlbumAdapter(
    private val onItemClick: (Uri) -> Unit, // 아이템 클릭 콜백
    private val onItemLongClick: (Uri) -> Unit // 아이템 길게 클릭 시 콜백
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    private val imageUris = mutableListOf<Uri>() // 이미지 URI 리스트
    var isSelectionMode = false // 다중 선택 모드 여부
    private val selectedUris = LinkedHashMap<Uri, Int>() // 선택된 이미지 URI와 순서 저장

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            // 이미지 로드
            Glide.with(binding.albumItem.context).load(uri).into(binding.albumItem)

            if (isSelectionMode) {
                binding.selectedImageNumber.visibility = View.VISIBLE
                if (selectedUris.containsKey(uri)) {
                    binding.selectedImageNumber.text = selectedUris[uri]?.toString()
                    binding.albumItem.alpha = 0.5f // 선택된 이미지 투명도 조절
                    binding.selectedImageNumber.isSelected = true // 선택 상태 설정
                } else {
                    binding.selectedImageNumber.text = "" // 번호 없음
                    binding.albumItem.alpha = 1.0f // 초기 상태
                    binding.selectedImageNumber.isSelected = false // 비선택 상태 설정
                }
            } else {
                // 단일 선택 모드
                binding.selectedImageNumber.visibility = View.GONE
                binding.albumItem.alpha = if (selectedUris.containsKey(uri)) 0.5f else 1.0f
            }

            // 클릭 리스너
            binding.root.setOnClickListener {
                onItemClick(uri)
            }

            // 길게 클릭 시 리스너 설정
            binding.root.setOnLongClickListener {
                onItemLongClick(uri)
                true
            }
        }
    }

    // 선택된 이미지 목록 업데이트
    fun updateSelection(selectedUris: Map<Uri, Int>) {
        this.selectedUris.clear()
        this.selectedUris.putAll(selectedUris)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        // 아이템 높이, 너비 동일
        val itemWidth = holder.itemView.context.resources.displayMetrics.widthPixels / 4
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = itemWidth
        holder.itemView.layoutParams = layoutParams

        holder.bind(imageUris[position])
    }

    // 이미지 리스트 설정
    fun submitList(images: List<Uri>) {
        imageUris.clear()
        imageUris.addAll(images)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = imageUris.size
}
