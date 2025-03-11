package com.pofo.backend.domain.resume.course.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CourseRequest(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val institution: String,

    @field:NotNull
    val startDate: LocalDate,

    @field:NotNull
    val endDate: LocalDate
)
