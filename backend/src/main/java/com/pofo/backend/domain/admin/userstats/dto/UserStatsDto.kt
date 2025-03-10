package com.pofo.backend.domain.admin.userstats.dto

import com.pofo.backend.domain.user.join.entity.User
import java.time.format.DateTimeFormatter

data class UserStatsDto(
    val id: Long? = null,
    val email: String? = null,
    val name: String? = null,
    val sex: User.Sex? = null,
    val nickname: String? = null,
    val age: String? = null,           // 생년월일을 포맷팅한 문자열
    val createdAt: String? = null,     // 가입일을 포맷팅한 문자열
    val lastLoginAt: String? = null,   // 마지막 로그인 시간 포맷팅한 문자열
    val jobInterest: String? = null,
    val userStatus: User.UserStatus? = null,
    val dormantFlg: String? = null,
    val dormantStartAt: String? = null, // 휴먼 처리 시작 시간 포맷팅한 문자열
    val dormantEndAt: String? = null    // 휴먼 처리 종료 시간 포맷팅한 문자열
) {
    // User 엔티티에서 DTO로 변환하는 생성자 (날짜 및 시간 포맷팅 처리)
    constructor(user: User) : this(
        id = user.getId(), // 명시적으로 getter 호출
        email = user.email,
        name = user.name,
        sex = user.sex,
        nickname = user.nickname,
        age = user.age?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        createdAt = user.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        lastLoginAt = user.lastLoginAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        jobInterest = user.jobInterest,
        userStatus = user.userStatus,
        dormantFlg = user.dormantFlg,
        dormantStartAt = user.dormantStartAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        dormantEndAt = user.dormantEndAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    )
}
