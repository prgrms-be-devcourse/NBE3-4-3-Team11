package com.pofo.backend.domain.resume.experience.dto

import java.time.LocalDate

data class ExperienceResponse(
    val name: String,
    val department: String,
    val position: String,
    val responsibility: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)
