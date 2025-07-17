package com.haco.shop.infrastructure.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig {
    @Value("\${spring.redis.redisson.config}")
    private lateinit var configYaml: String
}