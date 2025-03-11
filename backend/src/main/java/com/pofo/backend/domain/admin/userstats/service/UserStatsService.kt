package com.pofo.backend.domain.admin.userstats.service

import com.pofo.backend.domain.admin.userstats.repository.UserStatsRepository
import com.pofo.backend.domain.user.join.entity.User
import com.pofo.backend.domain.user.join.entity.User.Sex
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate
import com.pofo.backend.domain.admin.userstats.dto.UserStatsDto

@Service
class UserStatsService(
    private val userStatsRepository: UserStatsRepository
) {

    fun getUserStats(startDate: LocalDate?, endDate: LocalDate?, sex: Sex?, pageable: Pageable): Page<UserStatsDto> {
        val usersPage: Page<User> = when {
            sex != null && startDate != null && endDate != null ->
                userStatsRepository.findAllBySexAndCreatedAtBetween(sex, startDate, endDate, pageable)
            sex != null ->
                userStatsRepository.findAllBySex(sex, pageable)
            startDate != null && endDate != null ->
                userStatsRepository.findAllByCreatedAtBetween(startDate, endDate, pageable)
            else ->
                userStatsRepository.findAll(pageable)
        }

        // User 엔티티를 DTO로 변환
        return usersPage.map { UserStatsDto(it) }
    }

    fun getAllUsers(pageable: Pageable): Page<UserStatsDto> {
        return getUserStats(null, null, null, pageable)
    }
}
