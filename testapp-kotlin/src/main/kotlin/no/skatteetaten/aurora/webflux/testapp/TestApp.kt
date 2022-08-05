package no.skatteetaten.aurora.webflux.testapp

import brave.CurrentSpanCustomizer
import brave.SpanCustomizer
import brave.baggage.BaggageField
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KLIENTID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD
import no.skatteetaten.aurora.webflux.AuroraRequestParser.MELDINGSID_FIELD
import org.slf4j.MDC
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.sleuth.Span
import org.springframework.cloud.sleuth.instrument.messaging.DefaultMessageSpanCustomizer
import org.springframework.cloud.sleuth.instrument.messaging.MessageSpanCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

// Default profile disables zipkin integration, to enable zipkin start with the zipkin profile
// run docker-compose to start local zipkin
@SpringBootApplication
class TestMain

fun main(args: Array<String>) {
    SpringApplication.run(TestMain::class.java, *args)
}

@Configuration
class TestConfig {
    @Bean
    fun webClient(builder: WebClient.Builder) = builder.baseUrl("http://localhost:8080").build()

     @Bean
    fun messageSpanCustomizer() = object: MessageSpanCustomizer {
         override fun customizeHandle(
             spanCustomizer: Span,
             message: Message<*>?,
             messageChannel: MessageChannel?
         ): Span {
             return spanCustomizer
         }

         override fun customizeHandle(
             builder: Span.Builder,
             message: Message<*>?,
             messageChannel: MessageChannel?
         ): Span.Builder {
             return builder
         }

         override fun customizeReceive(
             builder: Span.Builder,
             message: Message<*>?,
             messageChannel: MessageChannel?
         ): Span.Builder {
             return builder
         }

         override fun customizeSend(
             builder: Span.Builder,
             message: Message<*>?,
             messageChannel: MessageChannel?
         ): Span.Builder {
             return builder
         }
     }
}

private val logger = KotlinLogging.logger {}

@RestController
class TestController(private val webClient: WebClient) {

    @GetMapping
    fun get(): Mono<Map<String, Any>> {
        val korrelasjonsid = BaggageField.getByName(KORRELASJONSID_FIELD)
        checkNotNull(korrelasjonsid)
        check(korrelasjonsid.value == MDC.get(KORRELASJONSID_FIELD))

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
        checkNotNull(headers[KORRELASJONSID_FIELD])
        checkNotNull(headers[MELDINGSID_FIELD])
        checkNotNull(headers[KLIENTID_FIELD])
        checkNotNull(headers[USER_AGENT])
        logger.info("MDC: ${MDC.get("Korrelasjonsid")}")
        return headers.toSingleValueMap().toMutableMap().apply {
            put("MDC-$KORRELASJONSID_FIELD", MDC.get("Korrelasjonsid"))
        }
    }
}
