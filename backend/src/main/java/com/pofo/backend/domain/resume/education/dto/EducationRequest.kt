package com.pofo.backend.domain.resume.education.dto

import com.pofo.backend.domain.resume.education.entity.Education
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.*

data class EducationRequest(
    @NotBlank
    var name: String,

    @NotBlank
    var major: String,

    @NotNull
    var startDate: LocalDate,

    @NotNull
    var endDate: LocalDate,

    @NotBlank
    var status: String,// 상태 (EXPECTED, GRADUATED, ENROLLED, REST)
) {
    val statusEnum: Education.Status
        get() = Education.Status.valueOf(status!!.uppercase(Locale.getDefault()))
}
