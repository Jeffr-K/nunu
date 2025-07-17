package com.haco.shop.modules.member.infrastructure.adapter.oauth2.facebook

data class FacebookUserInfoResponse(
    val id: String,
    val name: String?,
    val email: String?,
    val picture: FacebookPicture?
)
