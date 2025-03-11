package com.pofo.backend.domain.resume.activity.award.entity

import com.pofo.backend.common.jpa.entity.BaseTime
import com.pofo.backend.domain.resume.activity.activity.entity.Activity
import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDate

@Entity
@Table(name = "awards")
@Getter
class Award(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val institution: String,

    @Column(nullable = false)
    val awardDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    val activity: Activity
) : BaseTime()
