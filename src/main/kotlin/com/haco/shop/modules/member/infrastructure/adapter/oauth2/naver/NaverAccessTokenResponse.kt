package com.haco.shop.modules.member.infrastructure.adapter.oauth2.naver

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverAccessTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Int
)