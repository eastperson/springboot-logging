package com.ep.springbootlogging.exception

import org.springframework.http.HttpStatus

class CustomException(
    val status: HttpStatus,
    val code: Int? = null,
    val traceId: String? = null,
    val message: String? = null
)
