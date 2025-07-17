package com.haco.shop.modules.member.interfaces.controller

import com.haco.shop.infrastructure.utils.jwt.Tokens
import com.haco.shop.modules.member.core.service.NaverSocialLoginCredentials
import com.haco.shop.modules.member.core.service.GoogleSocialLoginCredentials
import com.haco.shop.modules.member.core.service.SocialOAuth2Service
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/oauth2")
@Tag(name = "OAuth2 인증", description = "소셜 로그인 관련 API")
class SocialOAuth2Controller(
    private val socialOAuth2Service: SocialOAuth2Service
) {
    private val logger = LoggerFactory.getLogger(SocialOAuth2Controller::class.java)

    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인 콜백", description = "카카오 OAuth2 인증 후 콜백을 처리하고 JWT 토큰을 반환합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "로그인 성공", content = [Content(schema = Schema(implementation = Tokens::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 코드"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun loginWithKakao(
        @Parameter(description = "카카오에서 발급된 인증 코드", required = true)
        @RequestParam code: String
    ): ResponseEntity<Tokens> {
        logger.info("카카오 로그인 콜백 요청 - code: {}", code)
        
        return try {
            val tokens = socialOAuth2Service.login(code, "kakao")
            logger.info("카카오 로그인 성공 - accessToken: {}...", tokens.accessToken.take(10))
            ResponseEntity.ok(tokens)
        } catch (e: IllegalArgumentException) {
            logger.error("카카오 로그인 실패 - 잘못된 요청: {}", e.message)
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("카카오 로그인 실패 - 예상치 못한 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/naver/callback")
    @Operation(summary = "네이버 로그인 콜백", description = "네이버 OAuth2 인증 후 콜백을 처리하고 JWT 토큰을 반환합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "로그인 성공", content = [Content(schema = Schema(implementation = Tokens::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 코드"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun loginWithNaver(
        @Parameter(description = "네이버에서 발급된 인증 코드", required = true)
        @RequestParam code: String
    ): ResponseEntity<Tokens> {
        logger.info("네이버 로그인 콜백 요청 - code: {}", code)
        
        return try {
            val tokens = socialOAuth2Service.login(code, "naver")
            logger.info("네이버 로그인 성공 - accessToken: {}...", tokens.accessToken.take(10))
            ResponseEntity.ok(tokens)
        } catch (e: IllegalArgumentException) {
            logger.error("네이버 로그인 실패 - 잘못된 요청: {}", e.message)
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("네이버 로그인 실패 - 예상치 못한 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/google/callback")
    @Operation(summary = "구글 로그인 콜백", description = "구글 OAuth2 인증 후 콜백을 처리하고 JWT 토큰을 반환합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "로그인 성공", content = [Content(schema = Schema(implementation = Tokens::class))]),
        ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 코드"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun loginWithGoogle(
        @Parameter(description = "구글에서 발급된 인증 코드", required = true)
        @RequestParam code: String
    ): ResponseEntity<Tokens> {
        logger.info("구글 로그인 콜백 요청 - code: {}", code)
        
        return try {
            val tokens = socialOAuth2Service.login(code, "google")
            logger.info("구글 로그인 성공 - accessToken: {}...", tokens.accessToken.take(10))
            ResponseEntity.ok(tokens)
        } catch (e: IllegalArgumentException) {
            logger.error("구글 로그인 실패 - 잘못된 요청: {}", e.message)
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("구글 로그인 실패 - 예상치 못한 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/javascript-key")
    @Operation(summary = "JavaScript SDK 키 조회", description = "소셜 로그인 프로바이더의 JavaScript SDK 키를 조회합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "키 조회 성공"),
        ApiResponse(responseCode = "400", description = "지원하지 않는 프로바이더"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun getOAuth2JavascriptKey(
        @Parameter(description = "소셜 로그인 프로바이더 (kakao, naver, google)", required = true)
        @RequestParam provider: String
    ): ResponseEntity<Map<String, String>> {
        logger.info("JavaScript SDK 키 조회 요청 - provider: {}", provider)
        
        return try {
            val key = socialOAuth2Service.getOAuth2JavascriptKey(provider)
            logger.info("JavaScript SDK 키 조회 성공 - provider: {}, key: {}...", provider, key.take(8))
            ResponseEntity.ok(mapOf("key" to key))
        } catch (e: IllegalArgumentException) {
            logger.error("JavaScript SDK 키 조회 실패 - 지원하지 않는 프로바이더: {}", provider)
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            logger.error("JavaScript SDK 키 조회 실패 - 예상치 못한 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/naver/credentials")
    @Operation(summary = "네이버 OAuth2 설정 정보 조회", description = "네이버 소셜 로그인에 필요한 설정 정보를 조회합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "설정 정보 조회 성공", content = [Content(schema = Schema(implementation = NaverSocialLoginCredentials::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun getNaverCredentials(): ResponseEntity<NaverSocialLoginCredentials> {
        logger.info("네이버 OAuth2 설정 정보 조회 요청")
        
        return try {
            val credentials = socialOAuth2Service.getNaverSocialLoginCredentials()
            logger.info("네이버 OAuth2 설정 정보 조회 성공 - clientId: {}...", credentials.clientId.take(8))
            ResponseEntity.ok(credentials)
        } catch (e: Exception) {
            logger.error("네이버 OAuth2 설정 정보 조회 실패 - 예상치 못한 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/google/credentials")
    @Operation(summary = "구글 OAuth2 설정 정보 조회", description = "구글 소셜 로그인에 필요한 설정 정보를 조회합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "설정 정보 조회 성공", content = [Content(schema = Schema(implementation = GoogleSocialLoginCredentials::class))]),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun getGoogleCredentials(): ResponseEntity<GoogleSocialLoginCredentials> {
        logger.info("구글 OAuth2 설정 정보 조회 요청")
        
        return try {
            val credentials = socialOAuth2Service.getGoogleSocialLoginCredentials()
            logger.info("구글 OAuth2 설정 정보 조회 성공 - clientId: {}...", credentials.clientId.take(8))
            ResponseEntity.ok(credentials)
        } catch (e: Exception) {
            logger.error("구글 OAuth2 설정 정보 조회 실패 - 예상치 못한 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}