package kr.co.company.capstone.dto.goal

data class Mate(
    val userId: Long,
    val mateId: Long,
    val nickname: String,
    var uploaded: Boolean
)
