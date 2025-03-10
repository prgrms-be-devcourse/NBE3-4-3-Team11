package com.pofo.backend.domain.admin.login.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
data class AdminLogoutResponse(
    val message: String? = null
)
