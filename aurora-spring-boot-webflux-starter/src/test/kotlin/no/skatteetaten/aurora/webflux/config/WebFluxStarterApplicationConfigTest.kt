package no.skatteetaten.aurora.webflux.config

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser
import no.skatteetaten.aurora.webflux.AuroraZipkinWebClientSender
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.sleuth.autoconfig.zipkin2.ZipkinAutoConfiguration
import org.springframework.cloud.sleuth.zipkin2.WebClientSender
import zipkin2.reporter.Sender

@SpringBootTest(
    classes = [
        WebClientAutoConfiguration::class,
        WebFluxStarterApplicationConfig::class,
        ZipkinAutoConfiguration::class
    ],
    properties = [
        "aurora.webflux.header.filter.enabled=false",
        "aurora.webflux.zipkin-sender.enabled=false"
    ]
)
class WebFluxStarterApplicationConfigTest {

    @Autowired(required = false)
    private var requestParser: AuroraRequestParser? = null

    @Autowired(required = false)
    private var auroraZipkinWebClientSender: AuroraZipkinWebClientSender? = null

    @Autowired(required = false)
    private var defaultSender: Sender? = null

    @Test
    fun `Header filter disabled`() {
        assertThat(requestParser).isNull()
    }

    @Test
    fun `Zipkin sender disabled`() {
        assertThat(auroraZipkinWebClientSender).isNull()
        assertThat(defaultSender).isNotNull().isInstanceOf(WebClientSender::class)
    }
}