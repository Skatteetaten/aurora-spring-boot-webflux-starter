package no.skatteetaten.aurora.webflux.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import brave.baggage.BaggageField
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD
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
        "span" to BaggageField.getByName(KORRELASJONSID_FIELD).value
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
            "aurora.webflux.header.filter.enabled=true"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class FilterEnabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `MDC and BaggageField is not empty`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNotNull().isNotEmpty()
            assertThat(response["span"]).isNotNull().isNotEmpty()
        }

        @Test
        fun `MDC and BaggageField is equal`() {
            val response = sendRequest(port)
            assertThat(response["mdc"]).isEqualTo(response["span"])
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, WebFluxStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=true",
            "aurora.webflux.header.filter.enabled=false"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class FilterDisabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `MDC and Korrelasjonsid is null`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNull()
            assertThat(response["span"]).isNull()
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, WebFluxStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=false",
            "aurora.webflux.header.filter.enabled=true"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class ZipkinDisabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `Korrelasjonsid is set`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNotNull().isNotEmpty()
            assertThat(response["span"]).isNotNull().isNotEmpty()
        }
    }

    @Nested
    @SpringBootTest(
        classes = [RequestTestMain::class, WebFluxStarterApplicationConfig::class],
        properties = [
            "spring.zipkin.enabled=false",
            "aurora.webflux.header.filter.enabled=false"
        ],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
    )
    inner class ZipkinAndFilterDisabled {
        @LocalServerPort
        private var port: Int = 0

        @Test
        fun `Korrelasjonsid is null`() {
            val response = sendRequest(port)

            assertThat(response["mdc"]).isNull()
            assertThat(response["span"]).isNull()
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