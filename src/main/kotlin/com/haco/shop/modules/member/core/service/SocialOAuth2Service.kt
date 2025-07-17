package com.haco.shop.modules.member.core.service

import com.haco.shop.infrastructure.utils.jwt.JwtTokenService
import com.haco.shop.infrastructure.utils.jwt.Tokens
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.kakao.KakaoAccessTokenResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.kakao.KakaoUserInfoResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.naver.NaverAccessTokenResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.naver.NaverUserInfoResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.google.GoogleAccessTokenResponse
import com.haco.shop.modules.member.infrastructure.adapter.oauth2.google.GoogleUserInfoResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

data class NaverSocialLoginCredentials(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String
)

data class GoogleSocialLoginCredentials(
    val clientId: String,
    val redirectUri: String
)

@Service
class SocialOAuth2Service(
    @Qualifier("kakaoRestClient") private val kakaoRestClient: RestClient,
    @Qualifier("naverRestClient") private val naverRestClient: RestClient,
    @Qualifier("googleRestClient") private val googleRestClient: RestClient,
    private val memberService: MemberService,
    private val tokenService: JwtTokenService
) {

    @Value("\${spring.security.oauth2.client.registration.kakao.client-id}")
    private lateinit var kakaoClientId: String

    @Value("\${spring.security.oauth2.client.registration.kakao.client-secret}")
    private lateinit var kakaoClientSecret: String

    @Value("\${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private lateinit var kakaoRedirectUri: String

    @Value("\${kakao.javascript-sdk-key}")
    private lateinit var kakaoJavascriptKey: String

    @Value("\${spring.security.oauth2.client.registration.naver.client-id}")
    private lateinit var naverClientId: String

    @Value("\${spring.security.oauth2.client.registration.naver.client-secret}")
    private lateinit var naverClientSecret: String

    @Value("\${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private lateinit var naverRedirectUri: String

    // 구글 OAuth2 설정
    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private lateinit var googleClientId: String

    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private lateinit var googleClientSecret: String

    @Value("\${spring.security.oauth2.client.registration.google.redirect-uri}")
    private lateinit var googleRedirectUri: String

    fun login(authorizationCode: String, provider: String): Tokens {
        when (provider) {
            "kakao" -> {
                println("카카오 로그인 시도: authorizationCode = $authorizationCode")

                val tokenResponse = getKakaoAccessToken(authorizationCode)
                if (tokenResponse == null) {
                    println("카카오 액세스 토큰 발급 실패")
                    throw IllegalArgumentException("카카오 액세스 토큰 발급 실패")
                }
                println("카카오 액세스 토큰: ${tokenResponse.accessToken}")

                val userInfo = getKakaoUserInfo(tokenResponse.accessToken)
                if (userInfo == null) {
                    println("카카오 사용자 정보 조회 실패")
                    throw IllegalArgumentException("카카오 사용자 정보 조회 실패")
                }
                println("카카오 사용자 정보: $userInfo")

                this.memberService.register(userInfo = userInfo)
                val tokens = this.tokenService.generateTokens(
                    email = userInfo.kakaoAccount?.email ?: "",
                    exp = 3600
                )

                return tokens
            }
            "naver" -> {
                println("네이버 로그인 시도: authorizationCode = $authorizationCode")

                val tokenResponse = getNaverAccessToken(authorizationCode)
                if (tokenResponse == null) {
                    println("네이버 액세스 토큰 발급 실패")
                    throw IllegalArgumentException("네이버 액세스 토큰 발급 실패")
                }
                println("네이버 액세스 토큰: ${tokenResponse.accessToken}")

                val userInfo = getNaverUserInfo(tokenResponse.accessToken)
                if (userInfo == null) {
                    println("네이버 사용자 정보 조회 실패")
                    throw IllegalArgumentException("네이버 사용자 정보 조회 실패")
                }
                println("네이버 사용자 정보: $userInfo")

                this.memberService.registerWithNaver(userInfo = userInfo)
                val tokens = this.tokenService.generateTokens(
                    email = userInfo.response.email ?: "",
                    exp = 3600
                )

                return tokens
            }
            "google" -> {
                println("구글 로그인 시도: authorizationCode = $authorizationCode")

                val tokenResponse = getGoogleAccessToken(authorizationCode)
                if (tokenResponse == null) {
                    println("구글 액세스 토큰 발급 실패")
                    throw IllegalArgumentException("구글 액세스 토큰 발급 실패")
                }
                println("구글 액세스 토큰: ${tokenResponse.accessToken}")

                val userInfo = getGoogleUserInfo(tokenResponse.accessToken)
                if (userInfo == null) {
                    println("구글 사용자 정보 조회 실패")
                    throw IllegalArgumentException("구글 사용자 정보 조회 실패")
                }
                println("구글 사용자 정보: $userInfo")

                this.memberService.registerWithGoogle(userInfo = userInfo)
                val tokens = this.tokenService.generateTokens(
                    email = userInfo.email ?: "",
                    exp = 3600
                )
                // TODO: store tokens in Redis

                return tokens
            }
            else -> {
                throw IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: $provider")
            }
        }
    }

    private fun getKakaoAccessToken(authorizationCode: String): KakaoAccessTokenResponse? {
        val requestBody = getRequestBody(authorizationCode)

        return try {
            kakaoRestClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body<KakaoAccessTokenResponse>()
        } catch (e: Exception) {
            println("카카오 액세스 토큰 요청 실패: ${e.message}")
            null
        }
    }

    private fun getRequestBody(authorizationCode: String): LinkedMultiValueMap<String, String> {
        return LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", naverClientId)
            add("client_secret", naverClientSecret)
            add("redirect_uri", naverRedirectUri)
            add("code", authorizationCode)
        }
    }

    private fun getNaverAccessToken(authorizationCode: String): NaverAccessTokenResponse? {
        val requestBody = getRequestBody(authorizationCode)
        return try {
            naverRestClient.post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body<NaverAccessTokenResponse>()
        } catch (e: Exception) {
            println("네이버 액세스 토큰 요청 실패: ${e.message}")
            null
        }
    }

    private fun getGoogleAccessToken(authorizationCode: String): GoogleAccessTokenResponse? {
        val requestBody = this.getRequestBody(authorizationCode)

        return try {
            googleRestClient.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .body<GoogleAccessTokenResponse>()
        } catch (e: Exception) {
            println("구글 액세스 토큰 요청 실패: ${e.message}")
            null
        }
    }

    fun getOAuth2JavascriptKey(provider: String): String {
        println("OAuth2 JavaScript Key 요청: provider = $provider")
        when (provider) {
            "kakao" -> return kakaoJavascriptKey
            else -> throw IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: $provider")
        }
    }

    fun getNaverSocialLoginCredentials(): NaverSocialLoginCredentials {
        return NaverSocialLoginCredentials(
            clientId = naverClientId,
            clientSecret = naverClientSecret,
            redirectUri = naverRedirectUri
        )
    }

    fun getGoogleSocialLoginCredentials(): GoogleSocialLoginCredentials {
        return GoogleSocialLoginCredentials(
            clientId = googleClientId,
            redirectUri = googleRedirectUri
        )
    }

    fun getKakaoUserInfo(accessToken: String): KakaoUserInfoResponse? {
        return try {
            kakaoRestClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .body<KakaoUserInfoResponse>()
        } catch (e: Exception) {
            println("카카오 사용자 정보 조회 실패: ${e.message}")
            null
        }
    }

    fun getNaverUserInfo(accessToken: String): NaverUserInfoResponse? {
        return try {
            naverRestClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .body<NaverUserInfoResponse>()
        } catch (e: Exception) {
            println("네이버 사용자 정보 조회 실패: ${e.message}")
            null
        }
    }

    fun getGoogleUserInfo(accessToken: String): GoogleUserInfoResponse? {
        return try {
            RestClient.builder()
                .baseUrl("https://www.googleapis.com")
                .build()
                .get()
                .uri("/oauth2/v2/userinfo")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .body<GoogleUserInfoResponse>()
        } catch (e: Exception) {
            println("구글 사용자 정보 조회 실패: ${e.message}")
            null
        }
    }
}