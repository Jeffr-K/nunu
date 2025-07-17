package com.haco.shop.modules.member.infrastructure.adapter.oauth2.kakao

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoProfile(
    val nickname: String?,

    @JsonProperty("profile_image_url")
    val profileImageUrl: String?
)
