package com.haco.shop.infrastructure.utils.logger

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

val Any.logger: Logger
    get() = LogManager.getLogger(this::class.java)