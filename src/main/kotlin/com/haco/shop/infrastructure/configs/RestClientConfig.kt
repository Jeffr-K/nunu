package com.haco.shop.infrastructure.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig {


    @Bean
    fun restClient(): RestClient {
        return RestClient.builder()
            .requestFactory(
                SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(Duration.ofSeconds(10))
                    setReadTimeout(Duration.ofSeconds(30))
                }
            )
            .defaultHeader("User-Agent", "ssap/1.0")
            .build()
    }

    @Bean("kakaoRestClient")
    fun kakaoRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }

    @Bean("naverRestClient")
    fun naverRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://openapi.naver.com")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }

    @Bean("googleRestClient")
    fun googleRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://oauth2.googleapis.com")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }

    @Bean("facebookRestClient")
    fun facebookRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl("https://graph.facebook.com")
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}