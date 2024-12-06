package kr.co.company.capstone.dto.timeline

data class PostItem(
    val postId: Long,
    val mateId: Int,
    val uploaderNickname: String,
    val uploadAt: String, // 형식: "2024-11-19T13:02:26.4857567"
    val imageUrls: List<String>,
    val likedUserIds: MutableList<Long>,
    val content: String
)