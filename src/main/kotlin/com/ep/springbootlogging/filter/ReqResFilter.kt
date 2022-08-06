package com.ep.springbootlogging.filter

import com.ep.springbootlogging.exception.CustomException
import com.ep.springbootlogging.logger.logger
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.IOException
import java.lang.RuntimeException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class ReqResFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    val log = logger()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val traceId = UUID.randomUUID().toString()
        try {
            val requestWrapper = CachedBodyHttpServletRequest(request)
            requestWrapper.setAttribute("traceId", traceId)
            filterChain.doFilter(requestWrapper, response)
        } catch (e: Exception) {
            log.error(e.message)
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            val customException = CustomException(
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                traceId = traceId,
                message = "Internal Server Error"
            )
            try {
                response.writer.use {
                    it.print(objectMapper.writeValueAsString(customException))
                    it.flush()
                }
            } catch (e: IOException) {
                log.warn("IOException Occur")
                throw RuntimeException()
            }
        }
    }
}
