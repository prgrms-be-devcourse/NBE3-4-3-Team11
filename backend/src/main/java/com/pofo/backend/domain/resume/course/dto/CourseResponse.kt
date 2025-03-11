package com.pofo.backend.domain.resume.course.dto

import java.time.LocalDate

data class CourseResponse(
    val name: String,
    val institution: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)
