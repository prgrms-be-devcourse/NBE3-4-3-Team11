package com.pofo.backend.domain.user.mypage.service

import com.pofo.backend.domain.user.join.repository.UserRepository
import com.pofo.backend.domain.user.mypage.dto.MypageResponseDto
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class MypageService(private val userRepository: UserRepository) {

    fun getMypageData(email: String): MypageResponseDto {
        val user = userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("사용자를 찾을 수 없습니다.") }

        return MypageResponseDto(
            email = user.email,
            name = user.name,
            nickname = user.nickname,
            sex = user.sex.name,
            age = user.age.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            jobInterest = user.jobInterest ?: "정보 없음",
            userStatus = user.userStatus.name
        )
    }
}
