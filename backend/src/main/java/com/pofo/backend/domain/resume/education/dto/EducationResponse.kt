package com.pofo.backend.domain.resume.education.dto

import com.pofo.backend.domain.resume.education.entity.Education
import java.time.LocalDate

data class EducationResponse(
    val name: String,
    val major: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: Education.Status
)
