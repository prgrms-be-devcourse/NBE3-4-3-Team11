package com.pofo.backend.domain.resume.license.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.resume.entity.Resume
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "licenses")
class License(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var institution: String,

    @Column(nullable = false)
    var certifiedDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    var resume: Resume
) : BaseTime() {

}
