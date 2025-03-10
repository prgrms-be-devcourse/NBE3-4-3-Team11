package com.pofo.backend.domain.admin.userstats.dto

import com.pofo.backend.domain.user.join.entity.User
import java.time.format.DateTimeFormatter

data class UserStatsDto(
    var id: Long? = null,
    var email: String? = null,
    var name: String? = null,
    var sex: User.Sex? = null,
    var nickname: String? = null,
    var age: String? = null,          // 생년월일을 포맷팅한 문자열
    var createdAt: String? = null,    // 가입일을 포맷팅한 문자열
    var lastLoginAt: String? = null,  // 마지막 로그인 시간 포맷팅한 문자열
    var jobInterest: String? = null,
    var userStatus: User.UserStatus? = null,
    var dormantFlg: String? = null,
    var dormantStartAt: String? = null, // 휴먼 처리 시작 시간 포맷팅한 문자열
    var dormantEndAt: String? = null    // 휴먼 처리 종료 시간 포맷팅한 문자열
) {
    constructor(user: User) : this(
        id = user.id,
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
