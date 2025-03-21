package com.pofo.backend.domain.resume.experience.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.resume.entity.Resume
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "experiences")
class Experience(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var department: String,

    @Column(nullable = false)
    var position: String,

    @Column(nullable = false)
    var responsibility: String,

    @Column(nullable = false)
    var startDate: LocalDate,

    @Column(nullable = false)
    var endDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    var resume: Resume
) : BaseTime()
