//package com.pofo.backend.domain.board.service
//
//import com.pofo.backend.common.rsData.RsData
//import com.pofo.backend.common.security.CustomUserDetails
//import com.pofo.backend.domain.board.dto.BoardDeleteResponseDto
//import com.pofo.backend.domain.board.dto.BoardListResponseDto
//import com.pofo.backend.domain.board.dto.BoardRequestDto
//import com.pofo.backend.domain.board.dto.BoardResponseDto
//import com.pofo.backend.domain.board.entity.Board
//import com.pofo.backend.domain.board.repository.BoardRepository
//import com.pofo.backend.domain.user.join.entity.User
//import com.pofo.backend.domain.user.join.repository.UserRepository
//import jakarta.persistence.EntityNotFoundException
//import lombok.RequiredArgsConstructor
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Pageable
//import org.springframework.data.domain.Sort
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import java.util.*
//import java.util.stream.Collectors
//import kotlin.math.max
//
//@Service
//@RequiredArgsConstructor
//class BoardService {
//    private val boardRepository: BoardRepository? = null
//    private val usersRepository: UserRepository? = null
//
//    fun getAllPosts(page: Int, size: Int): RsData<BoardListResponseDto> {
//        var size = size
//        size = max(size.toDouble(), 1.0).toInt()
//        val pageable: Pageable =
//            PageRequest.of(max(page.toDouble(), 1.0) - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"))
//        val boardPage = boardRepository!!.findAll(pageable)
//
//        val boardResponses = boardPage.content.stream()
//            .map { board: Board? -> BoardResponseDto(board) }
//            .collect(Collectors.toList())
//
//        return RsData(
//            "200", "게시글 목록 조회 성공", BoardListResponseDto(
//                boardPage.number + 1,
//                boardPage.totalPages,
//                boardPage.totalElements,
//                boardResponses
//            )
//        )
//    }
//
//    fun getPostById(id: Long): RsData<BoardResponseDto> {
//        val board = findEntityOrThrow(boardRepository!!.findById(id), "게시글을 찾을 수 없습니다.")
//        return RsData("200", "게시글 조회 성공", BoardResponseDto(board))
//    }
//
//    @Transactional
//    fun createPost(requestDto: BoardRequestDto): RsData<BoardResponseDto> {
//        val user = currentUser
//
//        val board = Board.builder()
//            .user(user)
//            .title(requestDto.title)
//            .content(requestDto.content)
//            .build()
//
//        boardRepository!!.save(board)
//        return RsData("201", "게시글 작성 성공", BoardResponseDto(board))
//    }
//
//    @Transactional
//    fun updatePost(id: Long, requestDto: BoardRequestDto): RsData<BoardResponseDto> {
//        val currentUserId = currentUserId
//        val board = findEntityOrThrow(boardRepository!!.findById(id), "게시글을 찾을 수 없습니다.")
//
//        if (board.user.id != currentUserId) {
//            throw RuntimeException("403: 본인이 작성한 게시글만 수정할 수 있습니다.")
//        }
//
//        board.title = requestDto.title
//        board.content = requestDto.content
//        boardRepository.save(board)
//        return RsData("200", "게시글 수정 성공", BoardResponseDto(board))
//    }
//
//
//    //    @Transactional
//    //    public RsData<BoardDeleteResponseDto> deletePost(Long id) {
//    //        Long currentUserId = getCurrentUserId();
//    //        Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//    //
//    //        if (!board.getUser().getId().equals(currentUserId)) {
//    //            throw new RuntimeException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.");
//    //        }
//    //
//    //        boardRepository.delete(board);
//    //        return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
//    //    }
//    //@Transactional
//    //public RsData<BoardDeleteResponseDto> deletePost(Long id, BoardDeleteRequestDto requestDto) {
//    //    Board board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.");
//    //
//    //    // ✅ 요청 바디에서 받은 userId와 게시글 작성자의 userId 비교
//    //    if (!board.getUser().getId().equals(requestDto.getUserId())) {
//    //        throw new RuntimeException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.");
//    //    }
//    //
//    //    boardRepository.delete(board);
//    //    return new RsData<>("200", "게시글 삭제 성공", new BoardDeleteResponseDto("게시글이 삭제되었습니다."));
//    //}
//    @Transactional
//    fun deletePost(id: Long): RsData<BoardDeleteResponseDto> {
//        val currentUserId = currentUserId // 현재 로그인한 사용자의 ID 가져오기
//        val board = boardRepository!!.findById(id)
//            .orElseThrow { IllegalArgumentException("404: 게시글을 찾을 수 없습니다.") } // 게시글 존재 여부 확인
//
//        // 작성자 검증: 로그인한 사용자 ID와 게시글 작성자의 ID 비교
//        if (board.user.id != currentUserId) {
//            throw SecurityException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.") // 예외 타입 변경 (보안 예외)
//        }
//
//        boardRepository.delete(board) // 게시글 삭제
//
//        // 삭제 성공 메시지 반환
//        return RsData("200", "게시글 삭제 성공", BoardDeleteResponseDto("게시글이 성공적으로 삭제되었습니다."))
//    }
//
//
//    private val currentUserId: Long
//        get() {
//            val authentication = SecurityContextHolder.getContext().authentication
//
//            check(!(authentication == null || authentication.principal == null)) { "현재 인증된 사용자를 찾을 수 없습니다." }
//
//            if (authentication.principal is CustomUserDetails) {
//                return customUserDetails.getUser().getId()
//            }
//
//            throw IllegalStateException("사용자 ID를 가져올 수 없습니다.")
//        }
//
//    private val currentUser: User
//        get() {
//            val authentication = SecurityContextHolder.getContext().authentication
//
//            check(!(authentication == null || authentication.principal == null)) { "현재 인증된 사용자를 찾을 수 없습니다." }
//
//            if (authentication.principal is CustomUserDetails) {
//                return customUserDetails.getUser()
//            }
//
//            val userId = currentUserId
//            return usersRepository!!.findById(userId)
//                .orElseThrow { IllegalStateException("현재 사용자를 찾을 수 없습니다.") }
//        }
//
//    private fun <T> findEntityOrThrow(entity: Optional<T>, errorMessage: String): T {
//        return entity.orElseThrow { EntityNotFoundException("404: $errorMessage") }
//    }
//}

package com.pofo.backend.domain.board.service

import com.pofo.backend.common.rsData.RsData
import com.pofo.backend.domain.board.dto.*
import com.pofo.backend.domain.board.entity.Board
import com.pofo.backend.domain.board.repository.BoardRepository
import com.pofo.backend.domain.user.join.entity.User
import com.pofo.backend.domain.user.join.repository.UserRepository
import com.pofo.backend.common.security.CustomUserDetails
import org.springframework.data.domain.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityNotFoundException
import java.util.*

@Service
open class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    fun getAllPosts(page: Int, size: Int): RsData<BoardListResponseDto> {
        val pageSize = maxOf(size, 1)
        val pageable: Pageable = PageRequest.of(maxOf(page, 1) - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        val boardPage = boardRepository.findAll(pageable)

        val boardResponses = boardPage.content.map { BoardResponseDto(it) }

        return RsData(
            "200", "게시글 목록 조회 성공",
            BoardListResponseDto(boardPage.number + 1, boardPage.totalPages, boardPage.totalElements, boardResponses)
        )
    }

    fun getPostById(id: Long): RsData<BoardResponseDto> {
        val board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.")
        return RsData("200", "게시글 조회 성공", BoardResponseDto(board))
    }

    @Transactional
    open fun createPost(requestDto: BoardRequestDto): RsData<BoardResponseDto> {
        val user = getCurrentUser()
        val board = Board(user, requestDto.title, requestDto.content) // ✅ 정상적으로 생성 가능
        boardRepository.save(board)
        return RsData("201", "게시글 작성 성공", BoardResponseDto(board))
    }

    @Transactional
    open fun updatePost(id: Long, requestDto: BoardRequestDto): RsData<BoardResponseDto> {
        val currentUserId = getCurrentUserId()
        val board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.")

        if (board.user.id != currentUserId) {
            throw SecurityException("403: 본인이 작성한 게시글만 수정할 수 있습니다.")
        }

        board.title = requestDto.title
        board.content = requestDto.content
        boardRepository.save(board)
        return RsData("200", "게시글 수정 성공", BoardResponseDto(board))
    }

    @Transactional
    open fun deletePost(id: Long): RsData<BoardDeleteResponseDto> {
        val currentUserId = getCurrentUserId()
        val board = boardRepository.findById(id)
            .orElseThrow { IllegalArgumentException("404: 게시글을 찾을 수 없습니다.") }

        if (board.user.id != currentUserId) {
            throw SecurityException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.")
        }

        boardRepository.delete(board)
        return RsData("200", "게시글 삭제 성공", BoardDeleteResponseDto("게시글이 성공적으로 삭제되었습니다."))
    }

    private fun getCurrentUserId(): Long {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return (authentication.principal as? CustomUserDetails)?.user?.id
            ?: throw IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.")
    }

    private fun getCurrentUser(): User {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return (authentication.principal as? CustomUserDetails)?.user
            ?: userRepository.findById(getCurrentUserId())
                .orElseThrow { IllegalStateException("현재 사용자를 찾을 수 없습니다.") }
    }

    private fun <T> findEntityOrThrow(entity: Optional<T>, errorMessage: String): T {
        return entity.orElseThrow { EntityNotFoundException("404: $errorMessage") }
    }
}
