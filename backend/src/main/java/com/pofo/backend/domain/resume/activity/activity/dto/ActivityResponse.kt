package com.pofo.backend.domain.resume.activity.activity.dto

import com.pofo.backend.domain.resume.activity.award.dto.AwardResponse
import java.time.LocalDate

data class ActivityResponse(
    val name: String,
    val history: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val awards: Set<AwardResponse>
)
