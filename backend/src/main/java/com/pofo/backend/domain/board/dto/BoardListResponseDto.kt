
package com.pofo.backend.domain.board.dto

// 게시글 목록 조회(페이징, 게시글 목록) -> GET /api/v1/user/boards
data class BoardListResponseDto(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Long,
    val boards: List<BoardResponseDto> // 게시글 목록 (개별 게시글 정보 포함)
)