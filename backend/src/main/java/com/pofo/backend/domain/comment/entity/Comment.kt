package com.pofo.backend.domain.comment.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.inquiry.entity.Inquiry
import com.pofo.backend.domain.user.join.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "comments")
class Comment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    val inquiry: Inquiry,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String
) : BaseTime() {

    // 팩토리 메서드
    companion object {
        fun createComment(user: User, inquiry: Inquiry, content: String): Comment {
            return Comment(user, inquiry, content)
        }
    }

    fun update(content: String) {
        this.content = content
    }
}