package no.skatteetaten.aurora.webflux

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import zipkin2.codec.SpanBytesEncoder

class AuroraZipkinWebClientSenderTest {
    private val sender =
        AuroraZipkinWebClientSender(WebClient.create(), "http://localhost:9411", null, SpanBytesEncoder.JSON_V2)
    private val server = MockWebServer()

    @BeforeEach
    fun setUp() {
        server.start(9411)
    }

    @Test
    fun `Trace requests sent to zipkin`() {
        server.enqueue(MockResponse())
        server.enqueue(MockResponse().setResponseCode(500))

        sender.sendSpans(emptyList()).execute()
        sender.sendSpans(emptyList()).execute()

        val request1 = server.takeRequest()
        val request2 = server.takeRequest()

        assertThat(server.requestCount).isEqualTo(2)
        assertThat(request1.requestUrl.toString()).isEqualTo("http://localhost:9411/api/v2/spans")
        assertThat(request2).isNotNull()
    }
}