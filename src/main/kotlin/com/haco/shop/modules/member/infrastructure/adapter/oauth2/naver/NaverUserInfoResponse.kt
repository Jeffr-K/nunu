package com.haco.shop.modules.member.infrastructure.adapter.oauth2.naver

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverUserInfoResponse(
    @JsonProperty("resultcode")
    val resultCode: String,
    val message: String,
    val response: NaverUserInfo
)