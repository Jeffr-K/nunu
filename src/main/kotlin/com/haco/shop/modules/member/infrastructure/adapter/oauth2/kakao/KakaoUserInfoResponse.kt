package com.haco.shop.modules.member.infrastructure.adapter.oauth2.kakao

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfoResponse(
    val id: Long,
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount?,

    @JsonProperty("connected_at")
    val connectedAt: String?,
)
