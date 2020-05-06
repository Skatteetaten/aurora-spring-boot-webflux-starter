package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.execute
import no.skatteetaten.aurora.mockmvc.extensions.mockwebserver.url
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.MELDINGID_FIELD
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.USER_AGENT_FIELD
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class AuroraHeaderWebFilterTest {

    @Autowired
    private lateinit var webClient: WebClient

    @Test
    fun `Set Aurora headers on request`() {
        val server = MockWebServer()
        val request = server.execute("test") {
            webClient.get().uri(server.url).retrieve().bodyToMono<String>().block()
        }.first()

        val headers = request?.headers!!
        assertThat(headers[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[MELDINGID_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[USER_AGENT_FIELD]).isNotNull().isNotEmpty()
        assertThat(headers[USER_AGENT_FIELD]).isNotNull().isNotEmpty()
    }

    @Disabled("Not finished")
    @Test
    fun `Use same Korrelasjonsid as incoming request header`() {
        val server = MockWebServer()
        val request = server.execute("test") {
            webClient.get().uri(server.url).header(KORRELASJONSID_FIELD, "abc123").retrieve().bodyToMono<String>().block()
        }.first()

        val headers = request?.headers!!
        assertThat(headers[KORRELASJONSID_FIELD]).isNotNull().isNotEmpty()
    }
}