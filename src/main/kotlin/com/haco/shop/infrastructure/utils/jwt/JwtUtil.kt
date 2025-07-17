package com.haco.shop.infrastructure.utils.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${jwt.secret:hacoShopSecretKeyForJwtTokenGenerationAndValidation}")
    private lateinit var secret: String

    @Value("\${jwt.expiration:86400}")
    private var expiration: Long = 86400

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        claims["roles"] = userDetails.authorities.map { it.authority }
        return createToken(claims, userDetails.username)
    }

    fun generateToken(username: String, roles: List<String>): String {
        val claims = mutableMapOf<String, Any>()
        claims["roles"] = roles
        return createToken(claims, username)
    }

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        val now = Instant.now()
        val expiryDate = now.plus(expiration, ChronoUnit.SECONDS)

        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiryDate))
            .signWith(getSigningKey())
            .compact()
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    fun extractRoles(token: String): List<String> {
        val claims = extractAllClaims(token)
        @Suppress("UNCHECKED_CAST")
        return claims["roles"] as? List<String> ?: emptyList()
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
}