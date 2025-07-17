package com.haco.shop.infrastructure.utils.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.Date

data class Tokens(
    val accessToken: String,
    val refreshToken: String
)

@Service
class JwtTokenService {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String
    private val key: Key by lazy {
        if (secretKey.toByteArray().size < 32) {
            Jwts.SIG.HS512.key().build()
        } else {
            Keys.hmacShaKeyFor(secretKey.toByteArray())
        }
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param email 사용자 이메일
     * @param exp 액세스 토큰의 만료 시간 (초 단위)
     * @return 생성된 액세스 토큰과 리프레시 토큰을 포함하는 Tokens 객체
     */
    fun generateTokens(email: String, exp: Int): Tokens {
        try {
            val now = Date()
            val accessTokenExpiration = Date(now.time + exp * 1000L)
            val refreshTokenExpiration = Date(now.time + 7 * 24 * 60 * 60 * 1000L)

            val accessToken = Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(accessTokenExpiration)
                .signWith(key)
                .compact()

            val refreshToken = Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(refreshTokenExpiration)
                .signWith(key)
                .compact()

            return Tokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("토큰 생성 실패: ${e.message}")
        }
    }
}