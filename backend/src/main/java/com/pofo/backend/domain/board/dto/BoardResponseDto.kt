
package com.pofo.backend.domain.board.dto

import com.pofo.backend.domain.board.entity.Board
import java.time.LocalDateTime


data class BoardResponseDto(
    val id: Long?,  // 게시글 ID
    val title: String,
    val content: String,
    val createdAt: LocalDateTime?,  // 작성일자 필드 추가
    val updatedAt: LocalDateTime?,  // 수정일자 필드 추가
    val userId: Long?  // 작성자 ID 추가
) {
    constructor(board: Board) : this(
        id = board.id,
        title = board.title,
        content = board.content,
        createdAt = board.createdAt,
        updatedAt = board.updatedAt,
        userId = board.user.id  // :흰색_확인_표시: Reflection 없이 직접 접근
    )
}



// Base엔티티 @Getter적용, board.id 대신 board.getId() 사용, private필드에 접근불가
// createdAt, updatedAt도 board.getCreatedAt() 사용