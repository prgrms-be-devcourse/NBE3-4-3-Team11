package com.pofo.backend.domain.resume.activity.activity.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.activity.award.entity.Award
import com.pofo.backend.domain.resume.resume.entity.Resume
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "activities")
class Activity(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val history: String,

    @Column(nullable = false)
    val startDate: LocalDate,

    @Column(nullable = false)
    val endDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    val resume: Resume,

    @OneToMany(mappedBy = "activity", cascade = [CascadeType.ALL], orphanRemoval = true)
    val awards: Set<Award> = HashSet()
) : BaseTime() {
}
