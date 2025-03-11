package com.pofo.backend.domain.board.service

import com.pofo.backend.common.rsData.RsData
import com.pofo.backend.domain.board.dto.*
import com.pofo.backend.domain.board.entity.Board
import com.pofo.backend.domain.board.repository.BoardRepository
import com.pofo.backend.domain.user.join.entity.User
import com.pofo.backend.domain.user.join.repository.UserRepository
import com.pofo.backend.common.security.CustomUserDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.data.domain.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.persistence.EntityNotFoundException
import java.util.*

@Service
class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {

    fun getAllPosts(page: Int, size: Int): RsData<BoardListResponseDto> {
        val adjustedSize = maxOf(size, 1)
        val pageable: Pageable = PageRequest.of(maxOf(page, 1) - 1, adjustedSize, Sort.by(Sort.Direction.DESC, "createdAt"))
        val boardPage: Page<Board> = boardRepository.findAll(pageable)

        val boardResponses = boardPage.content.map { BoardResponseDto(it) }

        return RsData(
            resultCode = "200",
            message = "게시글 목록 조회 성공",
            data = BoardListResponseDto(
                boardPage.number + 1,
                boardPage.totalPages,
                boardPage.totalElements,
                boardResponses
            )
        )
    }

    fun getPostById(id: Long): RsData<BoardResponseDto> {
        val board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.")

        return RsData(
            resultCode = "200",
            message = "게시글 조회 성공",
            data = BoardResponseDto(board)
        )
    }

    @Transactional
    fun createPost(requestDto: BoardRequestDto): RsData<BoardResponseDto> {
        val user = getCurrentUser()

        val board = Board.builder()
            .user(user)
            .title(requestDto.title)
            .content(requestDto.content)
            .build()

        boardRepository.save(board)
        return RsData(
            resultCode = "201",
            message = "게시글 작성 성공",
            data = BoardResponseDto(board)
        )
    }

    @Transactional
    fun updatePost(id: Long, requestDto: BoardRequestDto): RsData<BoardResponseDto> {
        val currentUserId = getCurrentUserId()
        val board = findEntityOrThrow(boardRepository.findById(id), "게시글을 찾을 수 없습니다.")

        val userId = board.user?.id ?: 0L
        if (userId != currentUserId) {
            throw RuntimeException("403: 본인이 작성한 게시글만 수정할 수 있습니다.")
        }

        board.title = requestDto.title
        board.content = requestDto.content
        boardRepository.save(board)

        return RsData(
            resultCode = "200",
            message = "게시글 수정 성공",
            data = BoardResponseDto(board)
        )
    }

    @Transactional
    fun deletePost(id: Long): RsData<BoardDeleteResponseDto> {
        val currentUserId = getCurrentUserId()
        val board = boardRepository.findById(id)
            .orElseThrow { IllegalArgumentException("404: 게시글을 찾을 수 없습니다.") }

        val userId = board.user?.id ?: 0L
        if (userId != currentUserId) {
            throw SecurityException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.")
        }

        boardRepository.delete(board)

        return RsData(
            resultCode = "200",
            message = "게시글 삭제 성공",
            data = BoardDeleteResponseDto("게시글이 성공적으로 삭제되었습니다.")
        )
    }

    private fun getCurrentUserId(): Long {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        requireNotNull(authentication) { "현재 인증된 사용자를 찾을 수 없습니다." }
        requireNotNull(authentication.principal) { "현재 인증된 사용자를 찾을 수 없습니다." }

        return (authentication.principal as? CustomUserDetails)?.user?.id
            ?: throw IllegalStateException("사용자 ID를 가져올 수 없습니다.")
    }

    private fun getCurrentUser(): User {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        requireNotNull(authentication) { "현재 인증된 사용자를 찾을 수 없습니다." }
        requireNotNull(authentication.principal) { "현재 인증된 사용자를 찾을 수 없습니다." }

        return (authentication.principal as? CustomUserDetails)?.user
            ?: userRepository.findById(getCurrentUserId()).orElseThrow {
                IllegalStateException("현재 사용자를 찾을 수 없습니다.")
            }
    }

    private fun <T> findEntityOrThrow(entity: Optional<T>, errorMessage: String): T {
        return entity.orElseThrow { EntityNotFoundException("404: $errorMessage") } // ✅ 오류 수정!
    }
}
