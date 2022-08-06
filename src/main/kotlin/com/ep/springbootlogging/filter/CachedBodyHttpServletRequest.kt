package com.ep.springbootlogging.filter

import org.springframework.util.StreamUtils
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val cachedBody: ByteArray

    init {
        val requestInputStream = request.inputStream
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream)
    }

    override fun getInputStream(): ServletInputStream {
        return CachedBodyServletInputStream(this.cachedBody)
    }
}

class CachedBodyServletInputStream(cachedBody: ByteArray) : ServletInputStream() {

    private val cachedBodyInputStream: InputStream

    init {
        this.cachedBodyInputStream = ByteArrayInputStream(cachedBody)
    }

    override fun read(): Int = cachedBodyInputStream.read()

    override fun isFinished(): Boolean = cachedBodyInputStream.available() == 0

    override fun isReady(): Boolean = true

    override fun setReadListener(listener: ReadListener?) {
        TODO("Not yet implemented")
    }
}