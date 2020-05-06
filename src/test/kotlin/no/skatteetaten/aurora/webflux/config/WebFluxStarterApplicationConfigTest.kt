package no.skatteetaten.aurora.webflux.config

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter
import no.skatteetaten.aurora.webflux.AuroraRequestParser
import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

class WebFluxStarterApplicationConfigTest {

    @Nested
    @SpringBootTest(classes = [WebFluxStarterApplicationConfig::class])
    inner class Default {
        @Autowired
        private lateinit var filter: AuroraHeaderWebFilter

        @Autowired(required = false)
        private var webclientInterceptor: AuroraWebClientCustomizer? = null

        @Autowired
        private lateinit var spanInterceptor: AuroraRequestParser

        @Test
        fun `Initialize default config`() {
            assertThat(filter).isNotNull()
            assertThat(webclientInterceptor).isNull()
            assertThat(spanInterceptor).isNotNull()
        }
    }

    @Nested
    @SpringBootTest(
        classes = [WebFluxStarterApplicationConfig::class], properties = [
            "aurora.webflux.header.span.interceptor.enabled=false"
        ]
    )
    inner class SpanDisabled {

        @Autowired(required = false)
        private var spanInterceptor: AuroraRequestParser? = null

        @Test
        fun `Span interceptor disabled`() {
            assertThat(spanInterceptor).isNull()
        }
    }

    @Nested
    @SpringBootTest(
        classes = [WebFluxStarterApplicationConfig::class], properties = [
            "aurora.webflux.header.filter.enabled=false"
        ]
    )
    inner class FilterDisabled {

        @Autowired(required = false)
        private var filter: AuroraHeaderWebFilter? = null

        @Test
        fun `Header filter disabled`() {
            assertThat(filter).isNull()
        }
    }
}