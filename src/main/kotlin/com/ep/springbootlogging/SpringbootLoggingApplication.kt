package com.ep.springbootlogging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class SpringbootLoggingApplication

fun main(args: Array<String>) {
    runApplication<SpringbootLoggingApplication>(*args)
}
