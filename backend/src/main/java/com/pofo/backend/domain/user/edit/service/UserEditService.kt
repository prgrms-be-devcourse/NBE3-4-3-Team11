package com.pofo.backend.domain.user.edit.service

import com.pofo.backend.domain.user.edit.dto.UserProfileUpdateRequestDto
import com.pofo.backend.domain.user.join.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class UserEditService(
    val userRepository: UserRepository
) {

    @Transactional
    open fun updateUserProfile(
        email: String,
        request: UserProfileUpdateRequestDto
    ) {
        val user = userRepository.findByEmail(email)
            .orElseThrow{ RuntimeException("사용자를 찾을 수 없습니다.") }

        user.nickname = request.nickname
        user.jobInterest = request.jobInterest
        user.userStatus = request.getUserStatusEnum()

        userRepository.save(user)
    }
}
