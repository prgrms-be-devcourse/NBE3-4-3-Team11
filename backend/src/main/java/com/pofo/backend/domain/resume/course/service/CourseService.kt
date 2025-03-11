package com.pofo.backend.domain.resume.course.service

import com.pofo.backend.domain.resume.course.dto.CourseRequest
import com.pofo.backend.domain.resume.course.entity.Course
import com.pofo.backend.domain.resume.course.repository.CourseRepository
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val resumeRepository: ResumeRepository
) {

    fun addCourses(resumeId: Long, courseRequests: List<CourseRequest>) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        val courseEntities = courseRequests.map { courseRequest ->
            // 빌더 대신 직접 생성
            Course(
                name = courseRequest.name,
                institution = courseRequest.institution,
                startDate = courseRequest.startDate,
                endDate = courseRequest.endDate,
                resume = resume
            )
        }

        courseRepository.saveAll(courseEntities)
    }

    fun updateCourses(resumeId: Long, courseRequests: List<CourseRequest>) {
        courseRepository.deleteByResumeId(resumeId)
        addCourses(resumeId, courseRequests)
    }
}
