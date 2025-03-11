package com.pofo.backend.domain.resume.license.dto

import java.time.LocalDate

data class LicenseResponse(
    val name: String,
    val institution: String,
    val certifiedDate: LocalDate
)
