/*package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.MELDINGSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.USER_AGENT_FIELD
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@TestConfiguration
open class TestConfig {
    @Bean
    open fun webClientBuilder(customizer: WebClientCustomizer) = customizer.let {
        val builder = WebClient.builder()
        it.customize(builder)
        builder
    }

    @Bean
    open fun webClient(builder: WebClient.Builder) = builder.build()
}

@SpringBootTest(
    classes = [WebFluxStarterApplicationConfig::class, TestConfig::class],
    properties = ["aurora.webflux.header.webclient.interceptor.enabled=true"]
)
class AuroraHeaderWebFilterTest {

    @Autowired
    private lateinit var webClient: WebClient

    private val server = MockWebServer()

    @BeforeEach
    fun setUp() {
        server.start()
    }

    @AfterEach
    fun tearDown() {
        kotlin.runCatching {
            server.shutdown()
        }
    }

    @Test
    fun `Set Aurora headers on request`() {
        server.enqueue(MockResponse().setBody("test"))

        webClient.get().uri(server.url("/").toString()).retrieve().bodyToMono<String>().block()

        val request = server.takeRequest()

        val headers = request.headers
        assertThat(headers[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[MELDINGSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[USER_AGENT_FIELD]).isEqualTo("webflux-starter")
        assertThat(headers[KLIENTID_FIELD]).isEqualTo("webflux-starter")
    }
}*/
