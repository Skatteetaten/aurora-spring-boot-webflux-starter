package no.skatteetaten.aurora.webflux

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import no.skatteetaten.aurora.webflux.AuroraSpanProcessor.TRACE_TAG_CLUSTER
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import okhttp3.Protocol
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.sleuth.Tracer
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@SpringBootTest(
    classes = [AuroraRequestParserMain::class, WebFluxStarterApplicationConfig::class],
    properties = [
        "aurora.webflux.header.filter.enabled=true",
        "spring.sleuth.otel.exporter.otlp.enabled=true"
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class AuroraSpanProcessorTest {
    @LocalServerPort
    private var port: Int = 0

    private val server = MockWebServer()

    @BeforeEach
    fun setUp() {
        server.start(4317)
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get trace tags from request`() {
        server.enqueue(MockResponse())
        server.protocols = listOf(Protocol.H2_PRIOR_KNOWLEDGE)

        WebClient.create("http://localhost:$port/mdc")
            .get()
            .retrieve()
            .bodyToMono<Map<String, String>>()
            .block()!!

        val request = server.takeRequest()
        val body = request.body.readUtf8()

        assertThat(body).all {
            contains(TRACE_TAG_CLUSTER)
            contains("test-123")
            contains("test-dev")
        }
    }
}