package com.pofo.backend.domain.resume.education.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.resume.entity.Resume
import jakarta.persistence.*
import lombok.Getter
import java.time.LocalDate

@Entity
@Table(name = "educations")
@Getter
class Education(

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val major: String,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: Status,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    val resume: Resume

) : BaseTime() {

    enum class Status {
        EXPECTED,  // 예정
        GRADUATED,  // 졸업
        ENROLLED,  // 재학
        REST // 휴학
    }
}
