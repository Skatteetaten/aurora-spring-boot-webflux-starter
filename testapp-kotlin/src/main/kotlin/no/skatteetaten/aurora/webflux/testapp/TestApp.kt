package no.skatteetaten.aurora.webflux.testapp

import io.opentelemetry.api.baggage.Baggage
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KLIENTID
import no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KORRELASJONSID
import no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_MELDINGSID
import org.slf4j.MDC
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@SpringBootApplication
class TestMain

fun main(args: Array<String>) {
    SpringApplication.run(TestMain::class.java, *args)
}

@Configuration
class TestConfig {
    @Bean
    fun webClient(builder: WebClient.Builder) = builder.baseUrl("http://localhost:8080").build()
}

private val logger = KotlinLogging.logger {}

@RestController
class TestController(private val webClient: WebClient) {

    @GetMapping
    fun get(): Mono<Map<String, Any>> {
        val korrelasjonsid = Baggage.current().getEntryValue(HEADER_KORRELASJONSID)
        checkNotNull(korrelasjonsid)
        check(korrelasjonsid == MDC.get(HEADER_KORRELASJONSID))

        logger.info("Get request")
        return webClient.get().uri("/headers").retrieve().bodyToMono<Map<String, String>>().map {
            mapOf(
                "Korrelasjonsid fra innkommende request" to korrelasjonsid,
                "Request headers fra WebClient" to it
            )
        }
    }

    @GetMapping("/suspended")
    suspend fun getSuspended() = get().awaitSingle().also {
        logger.info("Suspended function called")
    }

    @GetMapping("/headers")
    fun getHeaders(@RequestHeader headers: HttpHeaders): Map<String, String> {
        checkNotNull(headers[HEADER_KORRELASJONSID])
        checkNotNull(headers[HEADER_MELDINGSID])
        checkNotNull(headers[HEADER_KLIENTID])
        checkNotNull(headers[USER_AGENT])
        logger.info("MDC: ${MDC.get("Korrelasjonsid")}")
        return headers.toSingleValueMap().toMutableMap().apply {
            put("MDC-$HEADER_KORRELASJONSID", MDC.get("Korrelasjonsid"))
        }
    }
}
