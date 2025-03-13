package com.pofo.backend.common.jpa.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class BaseTime : BaseEntity() {
    @CreatedDate
    open var createdAt: LocalDateTime? = null
        protected set

    @LastModifiedDate
    open var updatedAt: LocalDateTime? = null
        protected set
}