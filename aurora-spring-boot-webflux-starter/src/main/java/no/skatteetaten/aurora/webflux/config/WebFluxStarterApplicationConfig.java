package no.skatteetaten.aurora.webflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentelemetry.sdk.trace.SpanProcessor;
import no.skatteetaten.aurora.webflux.AuroraSpanProcessor;
import no.skatteetaten.aurora.webflux.AuroraWebFilter;

@ConditionalOnMissingClass("no.skatteetaten.aurora.mvc.config.MvcStarterApplicationConfig")
@Configuration
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

    /*
    @Bean
    @ConditionalOnProperty(prefix = "trace.auth", name = { "username", "password" })
    public ZipkinWebClientBuilderProvider zipkinWebClientBuilderProvider(
        @Value("${trace.auth.username}") String username,
        @Value("${trace.auth.password}") String password,
        @Value("${aurora.klientid:}") String klientid
    ) {
        WebClient.Builder builder = WebClient.builder();
        return () -> builder.defaultHeaders((headers) -> {
            headers.setBasicAuth(username, password);

            if (klientid != null && klientid.contains("/")) {
                String affiliation = klientid.substring(0, klientid.indexOf("/"));
                headers.set(HEADER_ORGID, affiliation);
            }
        });
    }
     */
}
