package com.pofo.backend.domain.resume.experience.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ExperienceRequest(

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val department: String,

    @field:NotBlank
    val position: String,

    @field:NotBlank
    val responsibility: String,

    @field:NotNull
    val startDate: LocalDate,

    @field:NotNull
    val endDate: LocalDate
)
