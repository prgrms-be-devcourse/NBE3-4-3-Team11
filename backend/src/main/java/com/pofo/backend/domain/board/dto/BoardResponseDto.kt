//package com.pofo.backend.domain.board.dto;
//
//import com.pofo.backend.domain.board.entity.Board;
//import lombok.Getter;
//import java.time.LocalDateTime;
//
//
//
////게시글 상세 조회 -> GET /api/v1/user/boards/{id}
//
//@Getter
//public class BoardResponseDto  {
//    private final Long id;  //게시글 ID
//    private final String title;
//    private final String content;
//    private final LocalDateTime createdAt;  // 작성일자 필드 추가
//    private final LocalDateTime updatedAt;  // 수정일자 필드 추가
//    private Long userId;  // ✅ 작성자 ID 추가
//
//
//
//    public BoardResponseDto(Board board) {
//        this.id = board.getId();
//        this.title = board.getTitle();
//        this.content = board.getContent();
//        this.createdAt = board.getCreatedAt();  // BaseTime의 필드 값 설정
//        this.updatedAt = board.getUpdatedAt();  // 수정일자 추가
//        this.userId = board.getUser().getId();  // ✅ 작성자의 userId 포함
//
//    }
//}
//
package com.pofo.backend.domain.board.dto

import com.pofo.backend.domain.board.entity.Board
import java.time.LocalDateTime
//
//// 게시글 상세 조회 -> GET /api/v1/user/boards/{id}
//data class BoardResponseDto(
//    val id: Long,  // 게시글 ID
//    val title: String,
//    val content: String,
//    val createdAt: LocalDateTime,  // 작성일자 필드 추가
//    val updatedAt: LocalDateTime,  // 수정일자 필드 추가
//    val userId: Long // ✅ 작성자 ID 추가
//) {
//    constructor(board: Board) : this(
//        id = board::class.java.getMethod("getId").invoke(board) as Long,  // ✅ Reflection 사용
//        title = board::class.java.getMethod("getTitle").invoke(board) as String,
//        content = board::class.java.getMethod("getContent").invoke(board) as String,
//        createdAt = board::class.java.getMethod("getCreatedAt").invoke(board) as LocalDateTime,
//        updatedAt = board::class.java.getMethod("getUpdatedAt").invoke(board) as LocalDateTime,
////        userId = (board::class.java.getMethod("getUser").invoke(board) as? User)?.id ?: 0L
//        userId = (board::class.java.getMethod("getUser").invoke(board) as? User)
//            ?.let { it::class.java.getMethod("getId").invoke(it) as Long } ?: 0L // ✅ User의 id도 Reflection 사용
//
//    )
//}

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