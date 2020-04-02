package no.skatteetaten.aurora

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotNull
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.lang.RuntimeException

@TestConfiguration
open class TestConfig {
    @Bean
    open fun meterRegistry() = SimpleMeterRegistry()
}

@SpringBootTest(classes = [TestConfig::class, AuroraMetrics::class])
class AuroraMetricsTest {

    @Autowired
    private lateinit var metrics: AuroraMetrics

    @Autowired
    private lateinit var registry: SimpleMeterRegistry

    @Test
    fun `Metrics registers operations timer and counters`() {
        metrics.withMetrics("unit-test") {
            metrics.status("1", AuroraMetrics.StatusValue.OK)
            metrics.status("2", AuroraMetrics.StatusValue.CRITICAL)
        }

        assertThat(registry.get("operations").timer()).isNotNull()
        assertThat(registry.get("statuses").counters()).hasSize(2)
    }

    @Test
    fun `Register timer when exception is thrown`() {
        kotlin.runCatching {
            metrics.withMetrics("unit-test") {
                throw RuntimeException("test exception")
            }
        }

        assertThat(registry.get("operations").timer()).isNotNull()
    }
}