package com.pofo.backend.domain.resume.activity.award.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate


data class AwardRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val institution: String,
    @field:NotNull val awardDate: LocalDate
)