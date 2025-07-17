package com.haco.shop.infrastructure.utils.jwt

data class JwtTokenSession(
    val userId: String,
    val email: String,
    val nickname: String,
    val provider: String,
    val loginTime: Long = System.currentTimeMillis(),
    val lastActivity: Long = System.currentTimeMillis()
)