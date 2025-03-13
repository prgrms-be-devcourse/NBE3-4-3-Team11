package com.pofo.backend.domain.comment.dto.request

import jakarta.validation.constraints.NotBlank

data class CommentCreateRequest(
    @field:NotBlank(message = "내용은 필수 항목입니다.")
    val content: String = ""  // 기본값 설정
)