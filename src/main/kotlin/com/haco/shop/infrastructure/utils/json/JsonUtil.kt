package com.haco.shop.infrastructure.utils.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JsonUtil {
    val mapper = jacksonObjectMapper()
}