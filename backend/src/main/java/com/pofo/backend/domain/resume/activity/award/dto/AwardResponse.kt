package com.pofo.backend.domain.resume.activity.award.dto

import java.time.LocalDate

data class AwardResponse(
    val name: String,
    val institution: String,
    val awardDate: LocalDate
)