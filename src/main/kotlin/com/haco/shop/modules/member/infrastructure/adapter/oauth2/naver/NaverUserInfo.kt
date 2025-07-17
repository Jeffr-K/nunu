package com.haco.shop.modules.member.infrastructure.adapter.oauth2.naver

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverUserInfo(
    val id: String,
    val nickname: String?,
    val name: String?,
    val email: String?,
    @JsonProperty("profile_image")
    val profileImage: String?,
    val age: String?,
    val gender: String?,
    val birthday: String?,
    val birthyear: String?,
    val mobile: String?
)