
package com.pofo.backend.domain.board.dto

import jakarta.validation.constraints.NotNull

// 게시글 생성 및 수정 시 사용되는 DTO
// POST /api/v1/user/boards -> 게시글 생성
// PATCH /api/v1/user/boards/{id} → 게시글 수정
data class BoardRequestDto(
    @field:NotNull(message = "제목은 비어 있을 수 없습니다.") // 제목 필수 입력
    val title: String,

    @field:NotNull(message = "내용은 비어 있을 수 없습니다.") // 내용 필수 입력
    val content: String
)