package com.haco.shop.infrastructure.configs

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("My Kotlin API")
                    .version("1.0.0")
                    .description("Kotlin Spring Boot REST API 문서")
                    .contact(
                        Contact()
                            .name("Developer")
                            .email("dev@example.com")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("개발 서버"),
                    Server().url("https://api.example.com").description("운영 서버")
                )
            )
            .components(
                Components()
                    .addSecuritySchemes("bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
    }
}