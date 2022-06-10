package no.skatteetaten.aurora.webflux.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import assertk.assertions.isTrue
import no.skatteetaten.aurora.webflux.AuroraRequestParser
import no.skatteetaten.aurora.webflux.TestConfig
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig.HEADER_ORGID
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.sleuth.zipkin2.ZipkinWebClientBuilderProvider
import org.springframework.http.HttpHeaders


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
        properties = ["trace.auth.username=test123"] // no password set
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
        properties = ["trace.auth.username=test123", "trace.auth.password=test234", "aurora.klientid=affiliation/app/1.2.3"]
    )
    @Nested
    inner class TraceAuthEnabled {
        @Autowired(required = false)
        private var zipkinBuilder: ZipkinWebClientBuilderProvider? = null

        @Test
        fun `Trace auth enabled and default Authorization header is set`() {
            val builder = zipkinBuilder!!.zipkinWebClientBuilder()
            builder.defaultHeaders {
                assertThat(it.containsKey(HttpHeaders.AUTHORIZATION)).isTrue()
            }
        }

        @Test
        fun `OrgId header set`() {
            val builder = zipkinBuilder!!.zipkinWebClientBuilder()
            builder.defaultHeaders {
                assertThat(it.getFirst(HEADER_ORGID)).isEqualTo("affiliation")
            }
        }
    }
}