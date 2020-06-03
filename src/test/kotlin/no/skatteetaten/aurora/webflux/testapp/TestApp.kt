package no.skatteetaten.aurora.webflux.testapp

import brave.propagation.ExtraFieldPropagation
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KLIENTID_FIELD
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.MELDINGID_FIELD
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

// Default profile will connect to zipkin (run docker-compose to start local zipkin)
// Start with no-zipkin profile to disable zipkin integration
@SpringBootApplication
open class TestMain

fun main(args: Array<String>) {
    SpringApplication.run(TestMain::class.java, *args)
}

@Configuration
open class TestConfig {
    @Bean
    open fun webClient(builder: WebClient.Builder) = builder.baseUrl("http://localhost:8080").build()
}

@RestController
open class TestController(private val webClient: WebClient) {

    @GetMapping
    fun get(): Mono<Map<String, Any>> {
        val korrelasjonsid = ExtraFieldPropagation.get(KORRELASJONSID_FIELD)
        checkNotNull(korrelasjonsid)

        return webClient.get().uri("/headers").retrieve().bodyToMono<Map<String, String>>().map {
            mapOf(
                "Korrelasjonsid fra WebFilter" to korrelasjonsid,
                "Request headers fra WebClient" to it
            )
        }
    }

    @GetMapping("/headers")
    fun getHeaders(@RequestHeader headers: HttpHeaders): Map<String, String> {
        checkNotNull(headers[KORRELASJONSID_FIELD])
        checkNotNull(headers[MELDINGID_FIELD])
        checkNotNull(headers[KLIENTID_FIELD])
        checkNotNull(headers[USER_AGENT])
        return headers.toSingleValueMap()
    }
}