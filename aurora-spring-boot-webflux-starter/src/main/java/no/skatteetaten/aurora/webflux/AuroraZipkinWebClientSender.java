package no.skatteetaten.aurora.webflux;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import zipkin2.Call;
import zipkin2.Callback;
import zipkin2.Span;
import zipkin2.codec.BytesEncoder;
import zipkin2.codec.Encoding;
import zipkin2.reporter.BytesMessageEncoder;
import zipkin2.reporter.Sender;

/**
 * Based on HttpSender from spring-cloud-sleuth.
 * <a href="https://github.com/spring-cloud/spring-cloud-sleuth/blob/3.1.x/spring-cloud-sleuth-zipkin
 *          /src/main/java/org/springframework/cloud/sleuth/zipkin2/HttpSender.java">
 *     HttpSender
 * </a>
 */
public class AuroraZipkinWebClientSender extends Sender {
    private static final Logger logger = LoggerFactory.getLogger(AuroraZipkinWebClientSender.class);
    private static final long DEFAULT_TIMEOUT = 1000;
    // This will drop a span larger than 5MiB. Note: values like 512KiB benchmark better.
    private static final int MESSAGE_MAX_BYTES = 5 * 1024 * 1024;
    private final WebClient webClient;
    private final String url;
    private final Encoding encoding;
    private final BytesMessageEncoder encoder;
    private transient boolean closeCalled = false;

    public AuroraZipkinWebClientSender(WebClient webClient, String baseUrl, String apiPath,
        BytesEncoder<Span> encoder) {
        this.webClient = webClient;
        this.url = buildUrlWithCustomPathIfNecessary(baseUrl, apiPath,
            baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "api/v2/spans");
        this.encoding = encoder.encoding();
        this.encoder = BytesMessageEncoder.forEncoding(encoding);
    }

    private String buildUrlWithCustomPathIfNecessary(final String baseUrl, final String customApiPath,
        final String defaultUrl) {
        if (Objects.nonNull(customApiPath)) {
            return baseUrl
                + (baseUrl.endsWith("/") || customApiPath.startsWith("/") || customApiPath.isEmpty() ? "" : "/")
                + customApiPath;
        }
        return defaultUrl;
    }

    @Override
    public Encoding encoding() {
        return encoding;
    }

    @Override
    public int messageMaxBytes() {
        return MESSAGE_MAX_BYTES;
    }

    @Override
    public int messageSizeInBytes(List<byte[]> spans) {
        return this.encoding().listSizeInBytes(spans);
    }

    @Override
    public void close() {
        closeCalled = true;
    }

    @Override
    public Call<Void> sendSpans(List<byte[]> encodedSpans) {
        if (this.closeCalled) {
            throw new IllegalStateException("close");
        }
        return new HttpPostCall(encoder.encode(encodedSpans));
    }

    private void post(byte[] json) {
        webClient
            .post()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .retrieve()
            .toBodilessEntity()
            .onErrorResume(error -> {
                logger.warn("Unable to send trace data: {}", error.getMessage());
                return Mono.empty();
            })
            .timeout(Duration.ofMillis(DEFAULT_TIMEOUT))
            .block();
    }

    class HttpPostCall extends Call.Base<Void> {
        private final byte[] message;

        HttpPostCall(byte[] message) {
            this.message = message;
        }

        protected Void doExecute() {
            post(this.message);
            return null;
        }

        @Override
        protected void doEnqueue(Callback<Void> callback) {
            try {
                post(this.message);
                callback.onSuccess(null);
            } catch (RuntimeException | Error e) {
                callback.onError(e);
            }
        }

        @Override
        public Call<Void> clone() {
            return new HttpPostCall(this.message);
        }
    }
}
