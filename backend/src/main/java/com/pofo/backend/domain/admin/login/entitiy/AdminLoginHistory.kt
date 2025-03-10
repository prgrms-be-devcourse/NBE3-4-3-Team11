package com.pofo.backend.domain.admin.login.entitiy

import com.pofo.backend.common.jpa.entity.BaseTime
import jakarta.persistence.*
import lombok.*

@Entity
@Table(name = "admin_login_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AdminLoginHistory : BaseTime() {
    @ManyToOne
    @JoinColumn(name = "admin_id", referencedColumnName = "id", nullable = false)
     var admin: Admin? = null // admin_id 외래 키 필드

    @Column(name = "failure_count", nullable = false, columnDefinition = "int default 0")
     var failureCount = 0

    @Column(name = "login_status", nullable = false)
     var loginStatus: Byte = 0 // TINYINT로 저장

    companion object {
        const val FAILED: Byte = 0
        const val SUCCESS: Byte = 1
    }
}