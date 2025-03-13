package com.pofo.backend.domain.resume.activity.activity.dto

import com.pofo.backend.domain.resume.activity.award.dto.AwardRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ActivityRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val history: String,
    @field:NotNull val startDate: LocalDate,
    @field:NotNull val endDate: LocalDate,
    val awards: List<AwardRequest> = emptyList()
)