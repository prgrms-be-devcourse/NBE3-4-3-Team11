package com.pofo.backend.domain.resume.education.dto

import com.pofo.backend.domain.resume.education.entity.Education
import java.time.LocalDate

data class EducationResponse(
    val name: String? = null,
    val major: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val status: Education.Status? = null
)
