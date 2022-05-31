package no.skatteetaten.aurora.webflux.config

import assertk.assertThat
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import no.skatteetaten.aurora.webflux.AuroraRequestParser
import no.skatteetaten.aurora.webflux.TestConfig
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.sleuth.zipkin2.ZipkinWebClientBuilderProvider
import java.net.http.HttpHeaders


class WebFluxStarterApplicationConfigTest {

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

    @SpringBootTest(
        classes = [WebFluxStarterApplicationConfig::class],
        properties = ["aurora.webflux.trace.auth.username=test123"] // no password set
    )
    @Nested
    inner class TraceAuthDisabled {
        @Autowired(required = false)
        private var zipkinBuilder: ZipkinWebClientBuilderProvider? = null

        @Test
        fun `Trace auth disabled`() {
            assertThat(zipkinBuilder).isNull()
        }
    }

    @SpringBootTest(
        classes = [WebFluxStarterApplicationConfig::class, TestConfig::class],
        properties = ["aurora.webflux.trace.auth.username=test123", "aurora.webflux.trace.auth.password=test234"]
    )
    @Nested
    inner class TraceAuthEnabled {
        @Autowired(required = false)
        private var zipkinBuilder: ZipkinWebClientBuilderProvider? = null

        @Test
        fun `Trace auth enabled and default Authorization header is set`() {
            val builder = zipkinBuilder!!.zipkinWebClientBuilder()
            builder.defaultHeaders {
                assertThat(it.containsKey(org.springframework.http.HttpHeaders.AUTHORIZATION)).isTrue()
            }
        }
    }

}