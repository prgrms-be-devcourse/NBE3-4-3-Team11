package com.pofo.backend.domain.resume.resume.service

import com.pofo.backend.domain.resume.activity.activity.service.ActivityService
import com.pofo.backend.domain.resume.course.service.CourseService
import com.pofo.backend.domain.resume.education.service.EducationService
import com.pofo.backend.domain.resume.experience.service.ExperienceService
import com.pofo.backend.domain.resume.language.service.LanguageService
import com.pofo.backend.domain.resume.license.service.LicenseService
import com.pofo.backend.domain.resume.resume.dto.request.ResumeCreateRequest
import com.pofo.backend.domain.resume.resume.dto.response.ResumeResponse
import com.pofo.backend.domain.resume.resume.entity.Resume
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.exception.UnauthorizedActionException
import com.pofo.backend.domain.resume.resume.mapper.ResumeMapper
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import com.pofo.backend.domain.skill.service.ResumeSkillService
import com.pofo.backend.domain.tool.service.ResumeToolService
import com.pofo.backend.domain.user.join.entity.User
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val activityService: ActivityService,
    private val courseService: CourseService,
    private val experienceService: ExperienceService,
    private val educationService: EducationService,
    private val licenseService: LicenseService,
    private val languageService: LanguageService,
    private val resumeMapper: ResumeMapper,
    private val resumeSkillService: ResumeSkillService,
    private val resumeToolService: ResumeToolService
) {

    @Transactional
    fun createResume(request: ResumeCreateRequest, user: User): Resume {
        val resume = buildResume(request, user)
        return saveResumeAndRelatedEntities(resume, request)
    }

    @Transactional
    fun updateResume(request: ResumeCreateRequest, user: User): Resume {
        val resume = resumeRepository.findByUser(user)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }
        resumeRepository.delete(resume)

        return createResume(request, user)
    }

    @Transactional
    fun deleteResume(user: User) {
        val resume = resumeRepository.findByUser(user)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        try {
            resumeRepository.delete(resume)
        } catch (e: DataAccessException) {
            throw ResumeCreationException("이력서 삭제 중 데이터베이스 오류가 발생했습니다.")
        }
    }

    private fun buildResume(request: ResumeCreateRequest, user: User): Resume {
        return Resume(
            user = user,
            name = request.name,
            birth = request.birth,
            number = request.number,
            email = request.email,
            address = request.address,
            addressDetail = request.addressDetail,
            gitAddress = request.gitAddress,
            blogAddress = request.blogAddress
        )
    }

    private fun saveResumeAndRelatedEntities(resume: Resume, request: ResumeCreateRequest): Resume {
        return try {
            resumeRepository.save(resume).apply {
                addRelatedEntities(this, request)
            }
        } catch (e: DataAccessException) {
            throw ResumeCreationException("데이터베이스 오류가 발생했습니다.")
        }
    }

    private fun addRelatedEntities(resume: Resume, request: ResumeCreateRequest) {
        request.activities?.let { activityService.updateActivities(resume.id!!, it) }
        request.courses?.let { courseService.updateCourses(resume.id!!, it) }
        request.experiences?.let { experienceService.updateExperiences(resume.id!!, it) }
        request.educations?.let { educationService.updateEducations(resume.id!!, it) }
        request.licenses?.let { licenseService.updateLicenses(resume.id!!, it) }
        request.languages?.let { languageService.updateLanguages(resume.id!!, it) }
        request.skills?.let { resumeSkillService.updateSkills(resume.id!!, it) }
        request.tools?.let { resumeToolService.updateTools(resume.id!!, it) }
    }

    @Transactional(readOnly = true)
    fun getResumeResponse(user: User): ResumeResponse {
        val resume = resumeRepository.findByUser(user)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }
        return resumeMapper.resumeToResumeResponse(resume)
    }
}
