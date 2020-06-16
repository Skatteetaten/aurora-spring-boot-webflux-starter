package no.skatteetaten.aurora.webflux.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import brave.propagation.ExtraFieldPropagation
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@SpringBootApplication
open class RequestTestMain

@RestController
open class RequestTestController {

    @GetMapping
    fun getText() = mapOf(
        "mdc" to MDC.get(KORRELASJONSID_FIELD),
        "span" to ExtraFieldPropagation.get(KORRELASJONSID_FIELD)
    ).also {
        LoggerFactory.getLogger(RequestTestController::class.java).info("Clearing MDC, content: $it")
        MDC.clear()
    }
}

class RequestTest {

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, WebFluxStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=true",
            "aurora.webflux.header.filter.enabled=true",
            "aurora.webflux.header.span.interceptor.enabled=true"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class FilterAndSpan {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `MDC and ExtraFields contains Korrelasjonsid`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNotNull().isNotEmpty()
            assertThat(response["span"]).isNotNull().isNotEmpty()
            assertThat(response["mdc"]).isEqualTo(response["span"])
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, WebFluxStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=true",
            "aurora.webflux.header.filter.enabled=true",
            "aurora.webflux.header.span.interceptor.enabled=false"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class FilterOnly {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `MDC contains Korrelasjonsid`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNotNull().isNotEmpty()
            assertThat(response["span"]).isNull()
        }

        @Test
        fun `MDC contains same Korrelasjonsid as incoming request`() {
            val response = sendRequest(port, mapOf(KORRELASJONSID_FIELD to "abc123"))

            assertThat(response["mdc"]).isEqualTo("abc123")
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, WebFluxStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=true",
            "aurora.webflux.header.filter.enabled=false",
            "aurora.webflux.header.span.interceptor.enabled=true"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class SpanOnly {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `Span contains Korrelasjonsid`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNull()
            assertThat(response["span"]).isNotNull().isNotEmpty()
        }
    }

    fun sendRequest(port: Int, headers: Map<String, String> = emptyMap()) = WebClient
        .create()
        .get()
        .uri("http://localhost:${port}")
        .headers {
            it.setAll(headers)
        }
        .retrieve()
        .bodyToMono<Map<String, String>>()
        .block()!!
}