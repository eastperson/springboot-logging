package com.ep.springbootlogging.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
class SampleController {

    @GetMapping("/sample")
    fun sample(@RequestBody sampleRequest: SampleRequest): ResponseEntity<*> {
        return ResponseEntity.ok(SampleResponse(status = "OK", date = LocalDateTime.now()))
    }

    @GetMapping("/exception")
    fun exception(): ResponseEntity<*> {
        throw RuntimeException("런타임 에러")
    }
}

data class SampleRequest(
    val name: String,
    val number: Int
)

data class SampleResponse(
    val status: String,
    val date: LocalDateTime
)
