package no.skatteetaten.aurora.webflux

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.MELDINGSID_FIELD
import no.skatteetaten.aurora.webflux.config.AuroraWebClientConfig
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.cloud.sleuth.autoconfig.instrument.reactor.TraceReactorAutoConfiguration
import org.springframework.cloud.sleuth.autoconfig.otel.OtelAutoConfiguration
import org.springframework.http.HttpHeaders

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
    classes = [WebFluxStarterApplicationConfig::class, AuroraWebClientConfig::class, TestConfig::class, OtelAutoConfiguration::class],
    properties = ["aurora.webflux.header.webclient.interceptor.enabled=true"]
)
open class AbstractAuroraHeaderWebFilterTest {

    @Autowired
    lateinit var webClient: WebClient

    val server = MockWebServer()

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
}

class AuroraHeaderWebFilterDefaultTest : AbstractAuroraHeaderWebFilterTest() {
    @Test
    fun `Set Aurora headers on request`() {
        server.enqueue(MockResponse().setBody("test"))

        webClient.get().uri(server.url("/").toString()).retrieve().bodyToMono<String>().block()

        val request = server.takeRequest()

        val headers = request.headers
        assertThat(headers[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[MELDINGSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[HttpHeaders.USER_AGENT]).isEqualTo("webflux-starter")
        assertThat(headers[KLIENTID_FIELD]).isEqualTo("webflux-starter")
    }
}

@TestPropertySource(properties = ["aurora.klientid=segment/webflux-starter/1.0.0"])
class AuroraHeaderWebFilterKlientIdEnvTest : AbstractAuroraHeaderWebFilterTest() {

    @Test
    fun `Set KlientID from env`() {
        server.enqueue(MockResponse().setBody("test"))

        webClient.get().uri(server.url("/").toString()).retrieve().bodyToMono<String>().block()

        val request = server.takeRequest()

        val headers = request.headers
        assertThat(headers[KLIENTID_FIELD]).isNotNull().isEqualTo("segment/webflux-starter/1.0.0")
        assertThat(headers[HttpHeaders.USER_AGENT]).isNotNull().isEqualTo("segment/webflux-starter/1.0.0")
    }
}
@TestPropertySource(properties = ["app.version=1.0.0"])
class AuroraHeaderWebFilterVersionEnvTest : AbstractAuroraHeaderWebFilterTest() {

    @Test
    fun `Set fallback klientID`() {
        server.enqueue(MockResponse().setBody("test"))

        webClient.get().uri(server.url("/").toString()).retrieve().bodyToMono<String>().block()

        val request = server.takeRequest()

        val headers = request.headers
        assertThat(headers[KLIENTID_FIELD]).isNotNull().isEqualTo("webflux-starter/1.0.0")
        assertThat(headers[HttpHeaders.USER_AGENT]).isNotNull().isEqualTo("webflux-starter/1.0.0")
    }
}
