package com.pofo.backend.domain.user.mypage.dto

data class MypageResponseDto(
    val email: String,
    val name: String,
    val nickname: String,
    val sex: String,
    val age: String,
    val jobInterest: String,
    val userStatus: String
)
