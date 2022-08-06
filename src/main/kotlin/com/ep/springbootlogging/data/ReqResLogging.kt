package com.ep.springbootlogging.data

import com.fasterxml.jackson.annotation.JsonProperty

data class ReqResLogging(
    @JsonProperty(value = "traceId")
    val traceId: String,
    @JsonProperty(value = "class")
    val className: String? = null,
    @JsonProperty(value = "http_method")
    val httpMethod: String? = null,
    @JsonProperty(value = "uri")
    val uri: String? = null,
    @JsonProperty(value = "method")
    val method: String? = null,
    @JsonProperty(value = "params")
    val params: Map<String, Any>? = null,
    @JsonProperty(value = "log_time")
    val logTime: String,
    @JsonProperty(value = "server_ip")
    val serverIp: String? = null,
    @JsonProperty(value = "device_type")
    val deviceType: String? = null,
    @JsonProperty(value = "request_body")
    val requestBody: Any? = null,
    @JsonProperty(value = "response_body")
    val responseBody: Any? = null,
    @JsonProperty(value = "elapsed_time")
    val elapsedTime: String? = null
)