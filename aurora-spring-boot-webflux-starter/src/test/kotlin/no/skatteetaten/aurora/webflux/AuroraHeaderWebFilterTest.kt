package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KLIENTID
import no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KORRELASJONSID
import no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_MELDINGSID
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import okhttp3.Protocol
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@SpringBootApplication
open class AuroraHeaderWebFilterMain

@RestController
open class AuroraHeaderWebFilterTestController {

    @GetMapping("/mdc")
    fun getMdcValues() = mapOf(
        HEADER_KORRELASJONSID to MDC.get(HEADER_KORRELASJONSID),
        HEADER_MELDINGSID to MDC.get(HEADER_MELDINGSID),
        HEADER_KLIENTID to MDC.get(HEADER_KLIENTID)
    ).also {
        LoggerFactory.getLogger(AuroraHeaderWebFilterTestController::class.java).info("MDC content: $it")
        MDC.clear()
    }
}

@SpringBootTest(
    classes = [AuroraHeaderWebFilterMain::class, WebFluxStarterApplicationConfig::class],
    properties = [
        "aurora.webflux.header.filter.enabled=true",
        "spring.sleuth.otel.exporter.otlp.enabled=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class AuroraHeaderWebFilterTest {
    @LocalServerPort
    private var port: Int = 0

    private val server = MockWebServer()

    @BeforeEach
    fun setUp() {
        server.start(4317)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Given request headers set same values on MDC and generate Korrelasjonsid`() {
        server.protocols = listOf(Protocol.H2_PRIOR_KNOWLEDGE)
        server.enqueue(MockResponse())

        val requestHeaders =
            WebClient.create("http://localhost:$port/mdc")
                .get()
                .header(HEADER_MELDINGSID, "meldingsid")
                .header(HEADER_KLIENTID, "klientid")
                .retrieve()
                .bodyToMono<Map<String, String>>()
                .block()!!

        assertThat(requestHeaders[HEADER_KORRELASJONSID]).isNotNull().isNotEmpty()
        assertThat(requestHeaders[HEADER_MELDINGSID]).isEqualTo("meldingsid")
        assertThat(requestHeaders[HEADER_KLIENTID]).isEqualTo("klientid")
    }
}