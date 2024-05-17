package kr.co.company.capstone.dto.goal

data class Mate(
    val userId: Int,
    val mateId: Long,
    val nickname: String,
    var uploaded: Boolean
)
