package no.skatteetaten.aurora.webflux.config

import assertk.assertThat
import assertk.assertions.isNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.sleuth.autoconfig.otel.OtelExporterProperties

@SpringBootTest(
    classes = [OtelExporterProperties::class, WebFluxStarterApplicationConfig::class],
    properties = ["aurora.webflux.header.filter.enabled=false"]
)
class WebFluxStarterApplicationConfigTest {

    @Autowired(required = false)
    private var requestParser: AuroraRequestParser? = null

    @Test
    fun `Header filter disabled`() {
        assertThat(requestParser).isNull()
    }
}
