
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

@Transactional
@Service
open class BoardService(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository
) {
    fun getAllPosts(page: Int, size: Int): RsData<BoardListResponseDto> {
        val pageable: Pageable = PageRequest.of(maxOf(page, 1) - 1, maxOf(size, 1), Sort.by(Sort.Direction.DESC, "createdAt"))
        val boardPage = boardRepository.findAll(pageable)

        val boardResponses = boardPage.content.map { BoardResponseDto(it) }

        return RsData("200", "게시글 목록 조회 성공", BoardListResponseDto(boardPage.number + 1, boardPage.totalPages, boardPage.totalElements, boardResponses))
    }

    fun getPostById(id: Long): RsData<BoardResponseDto> {
        val board = findEntityOrThrow(boardRepository.findById(id).orElse(null), "게시글을 찾을 수 없습니다.")
        return RsData("200", "게시글 조회 성공", BoardResponseDto(board))
    }


    fun createPost(requestDto: BoardRequestDto): RsData<BoardResponseDto> {
        val user = getCurrentUser()
        val board = Board(user, requestDto.title, requestDto.content)

        boardRepository.save(board)
        return RsData("201", "게시글 작성 성공", BoardResponseDto(board)) // ✅ 올바른 RsData 생성자 사용
    }


   fun updatePost(id: Long, requestDto: BoardRequestDto): RsData<BoardResponseDto> {
        val currentUserId = getCurrentUserId()
        val board = findEntityOrThrow(boardRepository.findById(id).orElse(null), "게시글을 찾을 수 없습니다.")

        if (board.user.id != currentUserId) {
            throw SecurityException("403: 본인이 작성한 게시글만 수정할 수 있습니다.")
        }

//        board.title = requestDto.title
//        board.content = requestDto.content
        board.setTitle(requestDto.title) // ✅ setter 사용하여 값 변경
        board.setContent(requestDto.content) // ✅ setter 사용하여 값 변경

        boardRepository.save(board)
        return RsData("200", "게시글 수정 성공", BoardResponseDto(board))
    }


  fun deletePost(id: Long): RsData<BoardDeleteResponseDto> { // ✅ data 없이 사용할 수 있도록 Void 타입 사용
        val currentUserId = getCurrentUserId()
        val board = boardRepository.findById(id)
            .orElseThrow { IllegalArgumentException("404: 게시글을 찾을 수 없습니다.") }

        if (board.user.id != currentUserId) {
            throw SecurityException("403: 본인이 작성한 게시글만 삭제할 수 있습니다.")
        }

        boardRepository.delete(board)
        return RsData("200", "게시글 삭제 성공") // ✅ data 인수 생략 가능
    }

    private fun getCurrentUserId(): Long {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return (authentication.principal as? CustomUserDetails)?.user?.id
            ?: throw IllegalStateException("현재 인증된 사용자를 찾을 수 없습니다.")
    }

    private fun getCurrentUser(): User {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        return (authentication.principal as? CustomUserDetails)?.user
            ?: userRepository.findById(getCurrentUserId()).orElseThrow { IllegalStateException("현재 사용자를 찾을 수 없습니다.") }
    }

    private fun <T> findEntityOrThrow(entity: T?, errorMessage: String): T {
        return entity ?: throw EntityNotFoundException("404: $errorMessage")
    }
}
