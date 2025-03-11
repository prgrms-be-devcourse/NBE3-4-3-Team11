package com.pofo.backend.domain.resume.activity.activity.service

import com.pofo.backend.domain.resume.activity.activity.dto.ActivityRequest
import com.pofo.backend.domain.resume.activity.activity.entity.Activity
import com.pofo.backend.domain.resume.activity.activity.repository.ActivityRepository
import com.pofo.backend.domain.resume.activity.award.service.AwardService
import com.pofo.backend.domain.resume.resume.entity.Resume
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import com.pofo.backend.domain.resume.resume.repository.ResumeRepository
import org.springframework.stereotype.Service

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val resumeRepository: ResumeRepository,
    private val awardService: AwardService
) {

    fun addActivities(resumeId: Long, activities: List<ActivityRequest>) {
        val resume: Resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeCreationException("이력서를 찾을 수 없습니다.") }

        val activityEntities = activities.map { activityRequest ->
            Activity(
                name = activityRequest.name,
                history = activityRequest.history,
                startDate = activityRequest.startDate,
                endDate = activityRequest.endDate,
                resume = resume
            )
        }

        activityRepository.saveAll(activityEntities)

        activityEntities.forEachIndexed { index, activity ->
            activity.id?.let { activityId ->
                activities[index].awards?.let { awards ->
                    awardService.addAwards(activityId, awards)
                }
            }
        }
    }

    fun updateActivities(resumeId: Long, activities: List<ActivityRequest>) {
        activityRepository.deleteByResumeId(resumeId)
        addActivities(resumeId, activities)
    }
}
