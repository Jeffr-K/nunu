package com.haco.shop.infrastructure.health

import com.haco.shop.infrastructure.utils.logger.logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/healthcheck")
class HealthController {

    @GetMapping
    fun healthcheck(): String {
        logger.info("Health check endpoint called ========================")
        return "OK"
    }
}