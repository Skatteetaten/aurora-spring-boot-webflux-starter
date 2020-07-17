package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.MELDINGID_FIELD
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
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
        MELDINGID_FIELD to MDC.get(MELDINGID_FIELD),
        KLIENTID_FIELD to MDC.get(KLIENTID_FIELD)
    )
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

    @Test
    fun `Given no request headers set Korrelasjonsid only on MDC`() {
        val requestHeaders =
            WebClient.create("http://localhost:$port/mdc")
                .get()
                .retrieve()
                .bodyToMono<Map<String, String>>()
                .block()!!

        assertThat(requestHeaders[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(requestHeaders[MELDINGID_FIELD]).isNull()
        assertThat(requestHeaders[KLIENTID_FIELD]).isNull()
    }

    @Test
    fun `Given request headers set same values on MDC`() {
        val requestHeaders =
            WebClient.create("http://localhost:$port/mdc")
                .get()
                .header(KORRELASJONSID_FIELD, "korrelasjonsid")
                .header(MELDINGID_FIELD, "meldingsid")
                .header(KLIENTID_FIELD, "klientid")
                .retrieve()
                .bodyToMono<Map<String, String>>()
                .block()!!

        assertThat(requestHeaders[KORRELASJONSID_FIELD]).isEqualTo("korrelasjonsid")
        assertThat(requestHeaders[MELDINGID_FIELD]).isEqualTo("meldingsid")
        assertThat(requestHeaders[KLIENTID_FIELD]).isEqualTo("klientid")
    }
}