package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClientRequestException
import java.net.URI

class AuroraTraceErrorHandlerTest {

    private val baseUrl = "http://localhost:9411"
    private val errorHandler = AuroraTraceErrorHandler(baseUrl)

    @Test
    fun `WebClientRequestException with zipkin uri is trace error`() {
        val exception = WebClientRequestException(RuntimeException(), HttpMethod.GET, URI(baseUrl), HttpHeaders())
        val isTraceError = errorHandler.isTraceError(exception)

        assertThat(isTraceError).isTrue()
    }

    @Test
    fun `WebClientRequestException with non-zipkin uri is not trace error`() {
        val exception = WebClientRequestException(RuntimeException(), HttpMethod.GET, URI("http://localhost:8080"), HttpHeaders())
        val isTraceError = errorHandler.isTraceError(exception)

        assertThat(isTraceError).isFalse()
    }

    @Test
    fun `RuntimeException is not trace error`() {
        val isTraceError = errorHandler.isTraceError(RuntimeException())

        assertThat(isTraceError).isFalse()
    }

    @Test
    fun `Exception with no cause Throwable is not trace error`() {
        val isTraceError = errorHandler.isTraceError(RuntimeException("", null))

        assertThat(isTraceError).isFalse()
    }
}