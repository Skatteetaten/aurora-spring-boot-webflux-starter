package no.skatteetaten.aurora.webflux.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.cloud.sleuth.http.HttpRequestParser;
import org.springframework.cloud.sleuth.instrument.web.HttpServerRequestParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import no.skatteetaten.aurora.webflux.AuroraRequestParser;
import no.skatteetaten.aurora.webflux.AuroraSpanHandler;
import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer;

@EnableConfigurationProperties(WebFluxStarterProperties.class)
@Configuration
public class WebFluxStarterApplicationConfig {
    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.webclient.interceptor", name = "enabled")
    public WebClientCustomizer webClientCustomizer(@Value("${spring.application.name}") String name) {
        return new AuroraWebClientCustomizer(name);
    }

    @Bean(HttpServerRequestParser.NAME)
    @ConditionalOnProperty(prefix = "aurora.webflux.header.filter", name = "enabled", matchIfMissing = true)
    public HttpRequestParser sleuthHttpServerRequestParser() {
        return new AuroraRequestParser();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.zipkin", name = "enabled", havingValue = "false", matchIfMissing = true)
    public AuroraSpanHandler auroraSpanHandler() {
        return new AuroraSpanHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public OtlpHttpSpanExporter otlpHttpSpanExporter() {
        return OtlpHttpSpanExporter
            .builder()
            .setEndpoint("https://trace.sits.no:55681/v1/traces")
            .setTimeout(Duration.ofSeconds(5)) // .addHeader("", "")
            .build();
    }
}
