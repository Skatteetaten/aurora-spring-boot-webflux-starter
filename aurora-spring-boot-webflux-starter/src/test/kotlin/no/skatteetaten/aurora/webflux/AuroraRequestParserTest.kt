package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.MELDINGSID_FIELD
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
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
open class AuroraRequestParserMain

@RestController
open class AuroraRequestParserTestController {

    @GetMapping("/mdc")
    fun getMdcValues() = mapOf(
        KORRELASJONSID_FIELD to MDC.get(KORRELASJONSID_FIELD),
        MELDINGSID_FIELD to MDC.get(MELDINGSID_FIELD),
        KLIENTID_FIELD to MDC.get(KLIENTID_FIELD)
    ).also {
        LoggerFactory.getLogger(AuroraRequestParserTestController::class.java).info("MDC content: $it")
        MDC.clear()
    }
}

@SpringBootTest(
    classes = [AuroraRequestParserMain::class, WebFluxStarterApplicationConfig::class],
    properties = [
        "spring.zipkin.enabled=true",
        "aurora.webflux.header.filter.enabled=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class AuroraRequestParserTest {
    @LocalServerPort
    private var port: Int = 0

    private val server = MockWebServer()

    @BeforeEach
    fun setUp() {
        server.start(9411)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Given request headers set same values on MDC and generate Korrelasjonsid fails if zipkin disabled`() {
        server.enqueue(MockResponse())

        val requestHeaders =
            WebClient.create("http://localhost:$port/mdc")
                .get()
                .header(MELDINGSID_FIELD, "meldingsid")
                .header(KLIENTID_FIELD, "klientid")
                .retrieve()
                .bodyToMono<Map<String, String>>()
                .block()!!

        assertThat(requestHeaders[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(requestHeaders[MELDINGSID_FIELD]).isEqualTo("meldingsid")
        assertThat(requestHeaders[KLIENTID_FIELD]).isEqualTo("klientid")
    }
}