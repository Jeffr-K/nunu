package com.haco.shop.modules.member.infrastructure.adapter.oauth2.google

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleAccessTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int,

    @JsonProperty("refresh_token")
    val refreshToken: String?,

    @JsonProperty("scope")
    val scope: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("id_token")
    val idToken: String?
)