package no.skatteetaten.aurora.webflux.config

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser
import no.skatteetaten.aurora.webflux.AuroraSpanProcessor
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.cloud.sleuth.autoconfig.otel.OtelAutoConfiguration


class WebFluxStarterApplicationConfigTest {

    @SpringBootTest(classes = [WebFluxStarterApplicationConfig::class, AuroraWebClientConfig::class, OtelAutoConfiguration::class])
    @Nested
    inner class WebClientConfig {

        @Autowired(required = false)
        private var webClientCustomizer: WebClientCustomizer? = null

        @Autowired(required = false)
        private var auroraSpanHandler: AuroraSpanProcessor? = null

        @Test
        fun `Load webclient customizer and span handler`() {
            assertThat(webClientCustomizer).isNotNull()
            assertThat(auroraSpanHandler).isNotNull()
        }
    }

    @SpringBootTest(
        classes = [WebFluxStarterApplicationConfig::class],
        properties = ["aurora.webflux.header.filter.enabled=false"]
    )
    @Nested
    inner class HeaderFilter {
        @Autowired(required = false)
        private var requestParser: AuroraRequestParser? = null

        @Test
        fun `Header filter disabled`() {
            assertThat(requestParser).isNull()
        }
    }
}