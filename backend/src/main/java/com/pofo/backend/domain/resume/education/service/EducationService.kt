package com.pofo.backend.domain.resume.education.service

import com.pofo.backend.domain.resume.education.dto.EducationRequest
import com.pofo.backend.domain.resume.education.entity.Education
import com.pofo.backend.domain.resume.education.repository.EducationRepository
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import org.springframework.stereotype.Service

@Service
class EducationService(
    private val educationRepository: EducationRepository,
    private val resumeRepository: ResumeRepository
) {

    fun addEducations(resumeId: Long, educationRequests: List<EducationRequest>) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        val educationEntities = educationRequests.map { educationRequest ->
            Education(
                name = educationRequest.name,
                major = educationRequest.major,
                startDate = educationRequest.startDate,
                endDate = educationRequest.endDate,
                status = educationRequest.statusEnum,
                resume = resume
            )
        }

        educationRepository.saveAll(educationEntities)
    }

    fun updateEducations(resumeId: Long, educationRequests: List<EducationRequest>) {
        educationRepository.deleteByResumeId(resumeId)
        addEducations(resumeId, educationRequests)
    }
}
