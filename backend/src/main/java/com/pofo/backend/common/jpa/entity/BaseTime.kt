package com.pofo.backend.common.jpa.entity

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import lombok.AccessLevel
import lombok.Getter
import lombok.Setter
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Getter
@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
open class BaseTime : BaseEntity() {
    @CreatedDate
    @Setter(AccessLevel.PRIVATE)
    val createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Setter(AccessLevel.PRIVATE)
    val updatedAt: LocalDateTime? = null
}