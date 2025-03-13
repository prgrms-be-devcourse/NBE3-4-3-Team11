package com.pofo.backend.domain.resume.resume.dto.response

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityResponse
import com.pofo.backend.domain.resume.course.dto.CourseResponse
import com.pofo.backend.domain.resume.education.dto.EducationResponse
import com.pofo.backend.domain.resume.experience.dto.ExperienceResponse
import com.pofo.backend.domain.resume.language.dto.LanguageResponse
import com.pofo.backend.domain.resume.license.dto.LicenseResponse
import java.time.LocalDate

data class ResumeResponse(
    val name: String,
    val birth: LocalDate,
    val number: String,
    val email: String,
    val address: String,
    val addressDetail: String,
    val gitAddress: String? = null,
    val blogAddress: String? = null,
    val activities: Set<ActivityResponse> = emptySet(),
    val courses: Set<CourseResponse> = emptySet(),
    val experiences: Set<ExperienceResponse> = emptySet(),
    val educations: Set<EducationResponse> = emptySet(),
    val licenses: Set<LicenseResponse> = emptySet(),
    val languages: Set<LanguageResponse> = emptySet(),
    val skills: Set<SkillResponse> = emptySet(),
    val tools: Set<ToolResponse> = emptySet()
)
