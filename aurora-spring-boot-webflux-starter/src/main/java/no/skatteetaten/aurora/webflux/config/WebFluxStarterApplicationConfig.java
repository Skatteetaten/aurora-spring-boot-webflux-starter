package no.skatteetaten.aurora.webflux.config;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.autoconfig.otel.OtelExporterProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import no.skatteetaten.aurora.webflux.AuroraSpanProcessor;
import no.skatteetaten.aurora.webflux.AuroraWebFilter;

@ConditionalOnMissingClass("no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig")
@Configuration
@EnableConfigurationProperties(OtelExporterProperties.class)
public class WebFluxStarterApplicationConfig {

    public static final String HEADER_ORGID = "X-Scope-OrgID";

    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.filter", name = "enabled", matchIfMissing = true)
    public AuroraWebFilter auroraWebFilter(Tracer tracer) {
        return new AuroraWebFilter(tracer);
    }

    @Bean
    public SpanProcessor auroraSpanHandler(
        @Value("${openshift.cluster:}") String cluster,
        @Value("${pod.name:}") String podName,
        @Value("${aurora.klientid:}") String klientid,
        // Using namespace to set environment to match what is implemented in splunk
        @Value("${pod.namespace:}") String environment
    ) {
        return new AuroraSpanProcessor(cluster, podName, klientid, environment);
    }

    // Taken from OtlpExporterConfiguration, adds custom OrgID header with affiliation and Authorization header
    @Bean
    @ConditionalOnClass(OtlpGrpcSpanExporter.class)
    @ConditionalOnProperty(value = "spring.sleuth.otel.exporter.otlp.enabled", matchIfMissing = true)
    public OtlpGrpcSpanExporter otelOtlpGrpcSpanExporter(
        @Value("${aurora.klientid:}") String klientid,
        @Value("${trace.auth.username:}") String username,
        @Value("${trace.auth.password:}") String password,
        OtelExporterProperties properties
    ) {
        OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder();
        String endpoint = properties.getOtlp().getEndpoint();
        if (StringUtils.hasText(endpoint)) {
            builder.setEndpoint(endpoint);
        }
        Long timeout = properties.getOtlp().getTimeout();
        if (timeout != null) {
            builder.setTimeout(timeout, TimeUnit.MILLISECONDS);
        }
        Map<String, String> headers = properties.getOtlp().getHeaders();
        if (!headers.isEmpty()) {
            headers.forEach(builder::addHeader);
        }

        addOrgIdHeader(klientid, builder);
        addAuthHeader(username, password, builder);

        return builder.build();
    }

    private static void addOrgIdHeader(String klientid, OtlpGrpcSpanExporterBuilder builder) {
        if (klientid != null && klientid.contains("/")) {
            String affiliation = klientid.substring(0, klientid.indexOf("/"));
            builder.addHeader(HEADER_ORGID, affiliation);
        }
    }

    private static void addAuthHeader(String username, String password, OtlpGrpcSpanExporterBuilder builder) {
        if (!username.isEmpty() && !password.isEmpty()) {
            builder.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Basic " + HttpHeaders.encodeBasicAuth(username, password, null)
            );
        }
    }
}
