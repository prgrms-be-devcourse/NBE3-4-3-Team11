package com.pofo.backend.domain.admin.login.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
data class AdminLoginRequest(
    val username: String? = null,
    val password: String? = null
) {
    val authenticationToken: UsernamePasswordAuthenticationToken
        get() = UsernamePasswordAuthenticationToken(username, password)
}