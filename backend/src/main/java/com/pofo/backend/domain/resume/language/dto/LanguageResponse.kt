package com.pofo.backend.domain.resume.language.dto

import java.time.LocalDate

data class LanguageResponse(
    val language: String,
    val name: String,
    val result: String,
    val certifiedDate: LocalDate
)
