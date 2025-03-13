package com.pofo.backend.domain.resume.experience.service

import com.pofo.backend.domain.resume.experience.dto.ExperienceRequest
import com.pofo.backend.domain.resume.experience.entity.Experience
import com.pofo.backend.domain.resume.experience.repository.ExperienceRepository
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import org.springframework.stereotype.Service

@Service
class ExperienceService(
    private val experienceRepository: ExperienceRepository,
    private val resumeRepository: ResumeRepository
) {

    fun addExperiences(resumeId: Long, experienceRequests: List<ExperienceRequest>) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        val experienceEntities = experienceRequests.map { experienceRequest ->
            Experience(
                name = experienceRequest.name,
                department = experienceRequest.department,
                position = experienceRequest.position,
                responsibility = experienceRequest.responsibility,
                startDate = experienceRequest.startDate,
                endDate = experienceRequest.endDate,
                resume = resume
            )
        }

        experienceRepository.saveAll(experienceEntities)
    }

    fun updateExperiences(resumeId: Long, experienceRequests: List<ExperienceRequest>) {
        experienceRepository.deleteByResumeId(resumeId)
        addExperiences(resumeId, experienceRequests)
    }
}
