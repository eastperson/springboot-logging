package com.ep.springbootlogging.aop

import com.ep.springbootlogging.data.ReqResLogging
import com.ep.springbootlogging.exception.CustomException
import com.ep.springbootlogging.logger.logger
import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.InetAddress
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest

@Component
@Aspect
class ApiReqResAspect(
    private val objectMapper: ObjectMapper
) {

    val log = logger()

    @Pointcut("within(com.ep.springbootlogging.api..*)")
    fun apiRestPointCut() {}

    @Around("apiRestPointCut()")
    fun reqResLogging(joinPoint: ProceedingJoinPoint): Any {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val traceId = request.getAttribute("traceId") as String
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name
        val params = getParams(request)

        val deviceType = request.getHeader("x-custom-device-type")
        val serverIp = InetAddress.getLocalHost().hostAddress

        val reqResLogging = ReqResLogging(
            traceId = traceId,
            className = className,
            httpMethod = request.method,
            uri = request.requestURI,
            method = methodName,
            params = params,
            logTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
            serverIp = serverIp,
            deviceType = deviceType,
            requestBody = objectMapper.readTree(request.inputStream.readBytes())
        )

        val start = System.currentTimeMillis()
        try {
            val result = joinPoint.proceed()
            val elapsedTime = System.currentTimeMillis() - start
            val elapsedTimeStr = "Method: $className.$methodName() execution time: ${elapsedTime}ms"

            val logging = when (result) {
                is ResponseEntity<*> -> reqResLogging.copy(responseBody = result.body, elapsedTime = elapsedTimeStr)
                else -> reqResLogging.copy(responseBody = "{}")
            }
            log.info(objectMapper.writeValueAsString(logging))
            return result
        } catch (e: Exception) {
            log.info(
                "{}",
                objectMapper.writeValueAsString(
                    reqResLogging.copy(
                        responseBody = CustomException(
                            status = HttpStatus.INTERNAL_SERVER_ERROR,
                            code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            traceId = traceId,
                            message = "서버에 일시적인 장애가 있습니다."
                        )
                    )
                )
            )
            throw e
        }
    }

    private fun getParams(request: HttpServletRequest): Map<String, String> {
        val jsonObject = mutableMapOf<String, String>()
        val paramNames = request.parameterNames
        while (paramNames.hasMoreElements()) {
            val paramName = paramNames.nextElement()
            val replaceParam = paramName.replace("\\.", "-")
            jsonObject[replaceParam] = request.getParameter(paramName)
        }
        return jsonObject
    }
}
