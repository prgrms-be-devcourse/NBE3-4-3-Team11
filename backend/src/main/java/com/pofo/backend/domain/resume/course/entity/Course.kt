package com.pofo.backend.domain.resume.course.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.resume.entity.Resume
import jakarta.persistence.*
import lombok.*
import java.time.LocalDate

@Entity
@Table(name = "courses")
@Getter
class Course(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var institution: String,

    @Column(nullable = false)
    var startDate: LocalDate,

    @Column(nullable = false)
    var endDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    var resume: Resume
) : BaseTime()
