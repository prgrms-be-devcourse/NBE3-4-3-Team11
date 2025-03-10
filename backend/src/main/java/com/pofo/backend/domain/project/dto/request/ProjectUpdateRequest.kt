package com.pofo.backend.domain.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate


data class ProjectUpdateRequest (

    @field:NotBlank
    val name: String,

    @field:NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val startDate: LocalDate,

    @field:NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val endDate: LocalDate,

    @field:NotNull
    val memberCount: Int,

    @field:NotBlank
    val position: String,

    val repositoryLink: String,

    @field:NotBlank
    val description: String,

    @field:NotBlank
    val imageUrl: String,

    val thumbnailPath: String,

    @field:NotEmpty
    val skills: List<String> = emptyList(),

    @field:NotEmpty
    val tools: List<String> = emptyList(),

    val isDeleted: Boolean
)
