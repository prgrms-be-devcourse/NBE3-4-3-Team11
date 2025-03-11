package com.pofo.backend.domain.resume.resume.dto.request

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityRequest
import com.pofo.backend.domain.resume.course.dto.CourseRequest
import com.pofo.backend.domain.resume.education.dto.EducationRequest
import com.pofo.backend.domain.resume.experience.dto.ExperienceRequest
import com.pofo.backend.domain.resume.language.dto.LanguageRequest
import com.pofo.backend.domain.resume.license.dto.LicenseRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class ResumeCreateRequest(
    @field:NotBlank val name: String,
    @field:NotNull val birth: LocalDate,
    @field:NotBlank val number: String,
    @field:NotBlank val email: String,
    @field:NotBlank val address: String,
    @field:NotBlank val addressDetail: String,
    val gitAddress: String?,
    val blogAddress: String?,
    val activities: List<ActivityRequest>?,
    val courses: List<CourseRequest>?,
    val experiences: List<ExperienceRequest>?,
    val educations: List<EducationRequest>?,
    val licenses: List<LicenseRequest>?,
    val languages: List<LanguageRequest>?,
    val skills: List<Long>?,
    val tools: List<Long>?
)
