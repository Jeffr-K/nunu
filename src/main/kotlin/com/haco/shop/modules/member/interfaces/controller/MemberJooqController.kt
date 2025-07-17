package com.haco.shop.modules.member.interfaces.controller

import com.haco.shop.infrastructure.jooq.repository.MemberJooqRepository
import com.haco.shop.infrastructure.jooq.generated.tables.pojos.Member
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
@RequestMapping("/api/members/jooq")
@Tag(name = "회원 관리 (JOOQ)", description = "JOOQ를 사용한 회원 관리 API")
class MemberJooqController(
    private val memberJooqRepository: MemberJooqRepository
) {
    private val logger = LoggerFactory.getLogger(MemberJooqController::class.java)

    @GetMapping("/{id}")
    @Operation(summary = "회원 조회", description = "JOOQ를 사용하여 ID로 회원 정보를 조회합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "회원 조회 성공", content = [Content(schema = Schema(implementation = Member::class))]),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun getMemberById(
        @Parameter(description = "회원 ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Member> {
        logger.info("JOOQ 회원 조회 요청 - ID: {}", id)
        
        return try {
            val member = memberJooqRepository.findById(id)
            if (member != null) {
                logger.info("JOOQ 회원 조회 성공 - ID: {}, Email: {}", id, member.email)
                ResponseEntity.ok(member)
            } else {
                logger.warn("JOOQ 회원 조회 실패 - 회원을 찾을 수 없음: {}", id)
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("JOOQ 회원 조회 중 예상치 못한 오류 - ID: {}, 오류: {}", id, e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "이메일로 회원 조회", description = "JOOQ를 사용하여 이메일로 회원 정보를 조회합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "회원 조회 성공", content = [Content(schema = Schema(implementation = Member::class))]),
        ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun getMemberByEmail(
        @Parameter(description = "회원 이메일", required = true)
        @PathVariable email: String
    ): ResponseEntity<Member> {
        logger.info("JOOQ 이메일로 회원 조회 요청 - Email: {}", email)
        
        return try {
            val member = memberJooqRepository.findByEmail(email)
            if (member != null) {
                logger.info("JOOQ 이메일로 회원 조회 성공 - Email: {}, ID: {}", email, member.id)
                ResponseEntity.ok(member)
            } else {
                logger.warn("JOOQ 이메일로 회원 조회 실패 - 회원을 찾을 수 없음: {}", email)
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("JOOQ 이메일로 회원 조회 중 예상치 못한 오류 - Email: {}, 오류: {}", email, e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping
    @Operation(summary = "모든 회원 조회", description = "JOOQ를 사용하여 모든 회원 정보를 조회합니다")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "회원 목록 조회 성공"),
        ApiResponse(responseCode = "500", description = "서버 오류")
    ])
    fun getAllMembers(): ResponseEntity<List<Member>> {
        logger.info("JOOQ 모든 회원 조회 요청")
        
        return try {
            val members = memberJooqRepository.findAll()
            logger.info("JOOQ 모든 회원 조회 성공 - 총 {} 명", members.size)
            ResponseEntity.ok(members)
        } catch (e: Exception) {
            logger.error("JOOQ 모든 회원 조회 중 예상치 못한 오류 - 오류: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}