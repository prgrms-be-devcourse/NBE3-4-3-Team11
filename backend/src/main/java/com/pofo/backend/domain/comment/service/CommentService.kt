package com.pofo.backend.domain.comment.service

import com.pofo.backend.domain.comment.dto.request.CommentCreateRequest
import com.pofo.backend.domain.comment.dto.request.CommentUpdateRequest
import com.pofo.backend.domain.comment.dto.response.CommentCreateResponse
import com.pofo.backend.domain.comment.dto.response.CommentDetailResponse
import com.pofo.backend.domain.comment.dto.response.CommentUpdateResponse
import com.pofo.backend.domain.comment.entity.Comment
import com.pofo.backend.domain.comment.exception.CommentException
import com.pofo.backend.domain.comment.repository.CommentRepository
import com.pofo.backend.domain.inquiry.exception.InquiryException
import com.pofo.backend.domain.inquiry.repository.InquiryRepository
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException
import com.pofo.backend.domain.user.join.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val inquiryRepository: InquiryRepository
) {

    @Transactional
    fun create(id: Long, commentCreateRequest: CommentCreateRequest, user: User): CommentCreateResponse? {

        val inquiry = inquiryRepository.findById(id)
            .orElseThrow { InquiryException("문의사항을 찾을 수 없습니다.") }

        return try {
            val comment = Comment(
                user = user,
                inquiry = inquiry,
                content = commentCreateRequest.content
            )
            commentRepository.save(comment)
            comment.id?.let { CommentCreateResponse(it) }
        } catch (e: Exception) {
            throw CommentException("댓글 생성 중 오류가 발생했습니다. 원인: ${e.message}")
        }
    }

    @Transactional
    fun update(inquiryId: Long, commentId: Long, commentUpdateRequest: CommentUpdateRequest, user: User): CommentUpdateResponse? {
        val comment = commentRepository.findByInquiryIdAndId(inquiryId, commentId)
            .orElseThrow { CommentException("해당 댓글을 찾을 수 없습니다.") }

        if (comment.user.id != user.id) {
            throw CommentException("댓글을 수정할 권한이 없습니다.")
        }

        return try {
            // update 메서드 호출
            comment.update(commentUpdateRequest.content)
            return comment.id?.let { CommentUpdateResponse(it) }
        } catch (e: Exception) {
            throw CommentException("댓글 수정 중 오류가 발생했습니다. 원인: ${e.message}")
        }
    }

    @Transactional
    fun delete(inquiryId: Long, commentId: Long, user: User) {
        val comment = commentRepository.findByInquiryIdAndId(inquiryId, commentId)
            .orElseThrow { CommentException("해당 댓글을 찾을 수 없습니다.") }

        if (comment.user.id != user.id) {
            throw UnauthorizedActionException("댓글을 삭제할 권한이 없습니다.")
        }

        try {
            commentRepository.delete(comment)
        } catch (e: Exception) {
            throw CommentException("댓글 삭제 중 오류가 발생했습니다. 원인: ${e.message}")
        }
    }

    @Transactional(readOnly = true)
    fun findByInquiryId(inquiryId: Long): List<CommentDetailResponse> {
        return commentRepository.findByInquiryId(inquiryId)
            .mapNotNull { comment ->
                comment.createdAt?.let { createdAt ->
                    CommentDetailResponse(comment.id, comment.content, createdAt)
                }
            }
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): CommentDetailResponse {
        val comment = commentRepository.findById(id)
            .orElseThrow { CommentException("해당 댓글을 찾을 수 없습니다.") }

        return CommentDetailResponse(
            id = comment.id,
            content = comment.content,
            createdAt = comment.createdAt ?: throw CommentException("댓글의 생성 시간이 존재하지 않습니다.")
        )
    }

    @Transactional(readOnly = true)
    fun count(): Long {
        return commentRepository.count()
    }
}