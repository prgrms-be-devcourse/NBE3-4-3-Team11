package com.pofo.backend.domain.project.dto.response;

import java.time.LocalDate

data class ProjectUpdateResponse(
    val projectId: Long,
    val name : String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val memberCount: Int,
    val position : String,
    val repositoryLink: String?,
    val description: String,
    val imageUrl: String,
    val thumbnailPath: String?,
    val skills: List<String> = emptyList(),
    val tools: List<String> = emptyList(),
    val isDeleted: Boolean
)
