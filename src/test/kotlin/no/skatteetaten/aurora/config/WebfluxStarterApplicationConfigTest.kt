package no.skatteetaten.aurora.config

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [WebfluxStarterApplicationConfig::class])
class WebfluxStarterApplicationConfigTest {
    @Autowired
    private lateinit var config: WebfluxStarterApplicationConfig

    @Value("\${management.server.port}")
    private lateinit var managementPort: String

    @Test
    fun `Initialize base config and load properties`() {
        assertThat(config).isNotNull()
        assertThat(managementPort).isEqualTo("8081")
    }
}
