package com.pofo.backend.domain.resume.language.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class LanguageRequest(
    @field:NotBlank val language: String,
    @field:NotBlank val name: String,
    @field:NotBlank val result: String,
    @field:NotNull val certifiedDate: LocalDate
)
