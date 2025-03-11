package com.pofo.backend.domain.user.join.service

import com.pofo.backend.domain.user.join.entity.User
import com.pofo.backend.domain.user.join.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DormantAccountService(
    private val userRepository: UserRepository
) {
    @Scheduled(cron = "0 */1 * * * ?")
    fun markDormantAccounts() {
        val threshold = LocalDateTime.now().minusMinutes(1)
        val inactiveUsers: List<User> = userRepository.findInactiveUsers(threshold)
        println("휴먼 처리 대상 사용자 수: ${inactiveUsers.size}")

        inactiveUsers.forEach { user ->
            user.dormantFlg = "Y"
            user.dormantStartAt = LocalDateTime.now()
            user.dormantEndAt = null
            userRepository.save(user)
        }
    }
}
