package com.pofo.backend.domain.admin.login.entitiy

import com.pofo.backend.common.jpa.entity.BaseTime
import jakarta.persistence.*
import lombok.*

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class Admin : BaseTime() {
    @Column(name = "username", nullable = false)
     var username: String? = null

    @Column(name = "password", nullable = false)
     var password: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
     var status: Status? = null

    @Column(name = "failure_count", nullable = false, columnDefinition = "int default 0")
     var failureCount = 0

    enum class Status {
        ACTIVE,
        INACTIVE
    }
}
