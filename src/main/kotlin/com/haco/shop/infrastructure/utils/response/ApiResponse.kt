package com.haco.shop.infrastructure.utils.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

/**
 * 공통 API 응답 모델
 * @param T 응답 데이터 타입
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        /**
         * 성공 응답 (데이터 있음)
         */
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        /**
         * 성공 응답 (데이터 없음)
         */
        fun success(message: String = "Success"): ApiResponse<Nothing> {
            return ApiResponse(
                success = true,
                message = message
            )
        }

        /**
         * 실패 응답
         */
        fun <T> failure(message: String, error: ErrorDetail? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                error = error
            )
        }

        /**
         * 실패 응답 (에러 코드 포함)
         */
        fun <T> failure(message: String, errorCode: String, details: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                error = ErrorDetail(errorCode, details)
            )
        }
    }
}

/**
 * 에러 상세 정보
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDetail(
    val code: String,
    val details: String? = null
)

/**
 * 페이지네이션 응답 모델
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val first: Boolean,
    val last: Boolean
)