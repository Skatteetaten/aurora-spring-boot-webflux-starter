package no.skatteetaten.aurora.webflux.zipkin

import assertk.Assert
import assertk.assertThat
import assertk.assertions.support.expected
import brave.sampler.Sampler
import com.fasterxml.jackson.databind.JsonNode
import no.skatteetaten.aurora.webflux.AuroraRequestParser.TAG_KORRELASJONS_ID
import no.skatteetaten.aurora.webflux.config.WebFluxStarterApplicationConfig
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.has
import org.awaitility.kotlin.untilCallTo
import org.awaitility.kotlin.withPollDelay
import org.awaitility.kotlin.withPollInterval
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import java.time.Duration

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

@SpringBootApplication
open class TestMain

@TestConfiguration
open class TestConfig {
    @Bean
    open fun defaultSampler(): Sampler {
        return Sampler.ALWAYS_SAMPLE
    }

    @Bean
    @Primary
    open fun webClient(builder: WebClient.Builder) = builder.build()
}

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
@Testcontainers
@SpringBootTest(
    classes = [TestConfig::class, TestMain::class, WebFluxStarterApplicationConfig::class],
    properties = ["spring.zipkin.enabled=true", "aurora.mvc.header.span.interceptor.enabled=true"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ZipkinIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var webClient: WebClient

    companion object {
        @Container
        val zipkin: KGenericContainer = KGenericContainer(
            "openzipkin/zipkin-slim:2"
        )
            .withExposedPorts(9411)
            .waitingFor(Wait.forHttp("/zipkin"))

        @JvmStatic
        @DynamicPropertySource
        fun zipkinProperties(registry: DynamicPropertyRegistry) {
            zipkin.start()
            registry.add("spring.zipkin.base-url") {
                "http://${zipkin.host}:${zipkin.firstMappedPort}"
            }
        }
    }

    @Test
    fun `Request registers tracing data in zipkin`() {
        webClient.get().uri("http://localhost:$port/test").exchangeToMono { Mono.just(it) }.block()

        await()
            .withPollDelay(Duration.ofSeconds(1)) // Wait initial
            .withPollInterval(Duration.ofMillis(100)) // Wait before retrying
            .untilCallTo {
                webClient.get()
                    .uri("http://localhost:${zipkin.firstMappedPort}/api/v2/spans?serviceName=webflux-starter")
                    .retrieve().bodyToMono<JsonNode>().block()
            } has { size() > 0 }

        val traces = webClient.get().uri("http://localhost:${zipkin.firstMappedPort}/api/v2/traces").retrieve()
            .bodyToMono<JsonNode>().block()!!

        assertThat(traces).containsKorrelasjonsidTag()
    }

    private fun Assert<JsonNode>.containsKorrelasjonsidTag() = given { actual ->
        if (actual.findValues("tags").any { it.has(TAG_KORRELASJONS_ID) }) return
        expected("response to contain tag $TAG_KORRELASJONS_ID")
    }
}