package com.haco.shop.infrastructure.utils.jwt

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class RedissonTokenService(
    private val redissonClient: RedissonClient
) {
    // 1. OAuth AccessToken 저장
    fun storeOAuthAccessToken(
        userId: String,
        provider: String,
        accessToken: String,
        expiresInSeconds: Long
    ) {
        val key = "oauth:access_token:${provider}:${userId}"
        val bucket = redissonClient.getBucket<String>(key)
        bucket.set(accessToken, Duration.ofSeconds(expiresInSeconds))
    }

    // 2. OAuth AccessToken 조회
    fun getOAuthAccessToken(userId: String, provider: String): String? {
        val key = "oauth:access_token:${provider}:${userId}"
        val bucket = redissonClient.getBucket<String>(key)
        return bucket.get()
    }

    // 3. JWT RefreshToken 저장 (분산 락 적용)
    fun storeRefreshTokenSafely(userId: String, refreshToken: String): Boolean {
        val lock = redissonClient.getLock("lock:refresh_token:$userId")

        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                val key = "jwt:refresh_token:${userId}"
                val bucket = redissonClient.getBucket<String>(key)
                bucket.set(refreshToken, Duration.ofDays(7))
                return true
            }
            return false
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    // 4. JWT RefreshToken 조회
    fun getRefreshToken(userId: String): String? {
        val key = "jwt:refresh_token:${userId}"
        val bucket = redissonClient.getBucket<String>(key)
        return bucket.get()
    }

    // 5. 토큰 블랙리스트 (로그아웃 시)
    fun blacklistToken(accessToken: String, expirationTime: Long) {
        val key = "blacklist:${accessToken}"
        val bucket = redissonClient.getBucket<String>(key)
        bucket.set("blacklisted", Duration.ofMillis(expirationTime))
    }

    // 6. 블랙리스트 확인
    fun isTokenBlacklisted(accessToken: String): Boolean {
        val key = "blacklist:${accessToken}"
        return redissonClient.getBucket<String>(key).isExists
    }

    // 7. 사용자 세션 정보 저장 (객체 저장)
    fun storeUserSession(userId: String, sessionData: JwtTokenSession) {
        val key = "session:${userId}"
        val bucket = redissonClient.getBucket<JwtTokenSession>(key)
        bucket.set(sessionData, Duration.ofHours(24))
    }

    // 8. 사용자 세션 정보 조회
    fun getUserSession(userId: String): JwtTokenSession? {
        val key = "session:${userId}"
        val bucket = redissonClient.getBucket<JwtTokenSession>(key)
        return bucket.get()
    }

    // 9. 중복 로그인 방지 (세마포어 사용)
    fun allowLogin(userId: String): Boolean {
        val semaphore = redissonClient.getSemaphore("login:$userId")
        semaphore.trySetPermits(1) // 동시 로그인 1개만 허용

        return semaphore.tryAcquire(1, TimeUnit.SECONDS)
    }

    // 10. 로그아웃 시 로그인 세마포어 해제
    fun releaseLogin(userId: String) {
        val semaphore = redissonClient.getSemaphore("login:$userId")
        semaphore.release()
    }

    // 11. 모든 토큰 삭제 (로그아웃)
    fun deleteAllTokens(userId: String, provider: String? = null) {
        val lock = redissonClient.getLock("lock:logout:$userId")

        try {
            if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                // OAuth 토큰 삭제
                if (provider != null) {
                    redissonClient.getBucket<String>("oauth:access_token:${provider}:${userId}").delete()
                }

                // JWT 리프레시 토큰 삭제
                redissonClient.getBucket<String>("jwt:refresh_token:${userId}").delete()

                // 세션 삭제
                redissonClient.getBucket<JwtTokenSession>("session:${userId}").delete()

                // 로그인 세마포어 해제
                releaseLogin(userId)
            }
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}
