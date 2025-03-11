package com.pofo.backend.domain.resume.language.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.resume.entity.Resume
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "languages")
class Language(
    @Column(nullable = false)
    var language: String,

    @Column(nullable = false)
    var result: String,

    @Column(nullable = false)
    var certifiedDate: LocalDate,

    @Column(nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    var resume: Resume? = null
) : BaseTime() {

}
