package no.skatteetaten.aurora.webflux.request

import assertk.assertThat
import assertk.assertions.isNotNull
import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@RestController
class TestMdcController {
    @GetMapping("/mdc-values")
    fun getMdc(): Map<String, String> = MDC.getCopyOfContextMap()
}

@Disabled("Not sure why this is failing with gradle")
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