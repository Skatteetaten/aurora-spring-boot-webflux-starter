package no.skatteetaten.aurora.webflux.request

import assertk.assertThat
import assertk.assertions.isNotNull
import brave.baggage.BaggageField
import brave.handler.SpanHandler
import brave.http.HttpRequestParser
import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@RestController
class TestMdcController {

    @Bean
    @Primary
    fun sleuthHttpServerRequestParserMdc() = HttpRequestParser { request, context, _ ->
        request.header("customHeader")
            ?.let { BaggageField.create("customField").updateValue(context, it) }
    }

    @Bean
    fun spanHandler(): SpanHandler = SpanHandler.NOOP

    @GetMapping("/mdc-values")
    fun getMdc(): Map<String, String> = MDC.getCopyOfContextMap()
}

@SpringBootTest(classes = [RequestTestMain::class, TestMdcController::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequestMDCTest {

    @LocalServerPort
    private lateinit var port: String

    @Test
    fun `Test MDC value for multiple requests`() {
        val restTemplate = RestTemplate()
        repeat(10) {
            val korrelasjonsid =
                restTemplate.getForObject<JsonNode>("http://localhost:$port/mdc-values").at("/Korrelasjonsid").textValue()
            assertThat(korrelasjonsid).isNotNull()
        }
    }

}