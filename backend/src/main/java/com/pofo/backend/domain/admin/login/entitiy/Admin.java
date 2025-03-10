package com.pofo.backend.domain.admin.login.entitiy;

import com.pofo.backend.common.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseTime {

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "failure_count", nullable = false, columnDefinition = "int default 0")
    private int failureCount = 0;

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
