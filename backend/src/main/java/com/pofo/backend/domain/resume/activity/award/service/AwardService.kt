package com.pofo.backend.domain.resume.activity.award.service

import com.pofo.backend.domain.resume.activity.activity.repository.ActivityRepository
import com.pofo.backend.domain.resume.activity.award.dto.AwardRequest
import com.pofo.backend.domain.resume.activity.award.entity.Award
import com.pofo.backend.domain.resume.activity.award.repository.AwardRepository
import com.pofo.backend.domain.resume.resume.exception.ResumeCreationException
import org.springframework.stereotype.Service

@Service
class AwardService(
    private val awardRepository: AwardRepository,
    private val activityRepository: ActivityRepository
) {

    fun addAwards(activityId: Long, awards: List<AwardRequest>) {
        val activity = activityRepository.findById(activityId)
            .orElseThrow { ResumeCreationException("대외활동 내역을 찾을 수 없습니다.") }

        val awardEntities = awards.map { awardRequest ->
            Award(
                name = awardRequest.name,
                institution = awardRequest.institution,
                awardDate = awardRequest.awardDate,
                activity = activity
            )
        }
        awardRepository.saveAll(awardEntities)
    }
}
