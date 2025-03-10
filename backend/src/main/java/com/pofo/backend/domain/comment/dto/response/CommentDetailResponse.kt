package com.pofo.backend.domain.comment.dto.response

import com.pofo.backend.domain.comment.entity.Comment
import java.time.LocalDateTime

data class CommentDetailResponse(
    val id: Long?,
    val content: String,
    val createdAt: LocalDateTime,
    val type: String = "comment"
) {
    companion object {
        fun from(comment: Comment): CommentDetailResponse {
            return CommentDetailResponse(
                id = comment.id,
                content = comment.content,
                createdAt = comment.createdAt
            )
        }
    }
}