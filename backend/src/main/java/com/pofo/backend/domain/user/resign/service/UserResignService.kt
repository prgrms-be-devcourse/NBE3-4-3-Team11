package com.pofo.backend.domain.user.resign.service

import com.pofo.backend.domain.user.join.repository.OauthRepository
import com.pofo.backend.domain.user.join.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
open class UserResignService(
    private val userRepository: UserRepository,
    private val oauthRepository: OauthRepository
) {
    @Transactional
    open fun deleteUserByEmail(email : String) {
        val targetUser = userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("사용자를 찾을 수 없습니다.") }

        oauthRepository.deleteByUser(targetUser)
        userRepository.delete(targetUser)
    }
}