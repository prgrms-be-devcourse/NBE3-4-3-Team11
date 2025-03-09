package com.pofo.backend.domain.user.edit.dto

import com.pofo.backend.domain.user.join.entity.User
import com.pofo.backend.domain.user.join.entity.User.UserStatus

data class UserProfileUpdateRequestDto(
    val nickname: String,
    val jobInterest: String,
    val userStatus: UserStatus
) {
    fun getUserStatusEnum(): UserStatus {
        return User.UserStatus.valueOf(userStatus.name)
    }
}
