package com.pofo.backend.domain.resume.license.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

class LicenseRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val institution: String,
    @field:NotNull val certifiedDate: LocalDate
)
