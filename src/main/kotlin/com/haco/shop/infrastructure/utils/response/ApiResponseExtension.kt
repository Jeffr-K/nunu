package com.haco.shop.infrastructure.utils.response

import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * ApiResponse 확장 함수들
 */

/**
 * ApiResponse를 ResponseEntity로 변환
 */
fun <T> ApiResponse<T>.toResponseEntity(httpStatus: HttpStatus = HttpStatus.OK): ResponseEntity<ApiResponse<T>> {
    val status = if (this.success) httpStatus else HttpStatus.BAD_REQUEST
    return ResponseEntity.status(status).body(this)
}

/**
 * 성공 응답을 ResponseEntity로 변환
 */
fun <T> T.toSuccessResponse(message: String = "Success"): ResponseEntity<ApiResponse<T>> {
    return ApiResponse.success(this, message).toResponseEntity()
}

/**
 * 실패 응답을 ResponseEntity로 변환
 */
fun <T> String.toFailureResponse(httpStatus: HttpStatus = HttpStatus.BAD_REQUEST): ResponseEntity<ApiResponse<T>> {
    return ApiResponse.failure<T>(this).toResponseEntity(httpStatus)
}

/**
 * Page 객체를 PageResponse로 변환
 */
fun <T> Page<T>.toPageResponse(): PageResponse<T> {
    return PageResponse(
        content = this.content,
        page = this.number,
        size = this.size,
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        first = this.isFirst,
        last = this.isLast
    )
}

/**
 * Page 객체를 ApiResponse로 변환
 */
fun <T> Page<T>.toApiResponse(message: String = "Success"): ApiResponse<PageResponse<T>> {
    return ApiResponse.success(this.toPageResponse(), message)
}