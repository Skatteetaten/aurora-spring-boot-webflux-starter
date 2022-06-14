package no.skatteetaten.aurora.webflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.cloud.sleuth.instrument.web.HttpServerRequestParser;
import org.springframework.cloud.sleuth.zipkin2.ZipkinWebClientBuilderProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import brave.http.HttpRequestParser;
import no.skatteetaten.aurora.webflux.AuroraRequestParser;
import no.skatteetaten.aurora.webflux.AuroraSpanHandler;
import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer;

@EnableConfigurationProperties(WebFluxStarterProperties.class)
@Configuration
public class WebFluxStarterApplicationConfig {

    public static final String HEADER_ORGID = "X-Scope-OrgID";

    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.webclient.interceptor", name = "enabled")
    public WebClientCustomizer webClientCustomizer(
        @Value("${spring.application.name:}") String appName,
        @Value("${app.version:}") String appVersion,
        @Value("${aurora.klientid:}") String klientIdEnv
    ) {
        String fallbackKlientId = appVersion.isBlank() ? appName : String.format("%s/%s", appName, appVersion);
        String klientId = klientIdEnv.isBlank() ? fallbackKlientId : klientIdEnv;
        return new AuroraWebClientCustomizer(klientId);
    }

    @Bean(HttpServerRequestParser.NAME)
    @ConditionalOnProperty(prefix = "aurora.webflux.header.filter", name = "enabled", matchIfMissing = true)
    public HttpRequestParser sleuthHttpServerRequestParser(
        @Value("${openshift.cluster:}") String cluster,
        @Value("${pod.name:}") String podName) {
        return new AuroraRequestParser(cluster, podName);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.zipkin", name = "enabled", havingValue = "false", matchIfMissing = true)
    public AuroraSpanHandler auroraSpanHandler() {
        return new AuroraSpanHandler();
    }

    @Bean
    @ConditionalOnProperty(prefix = "trace.auth", name = { "username", "password" })
    public ZipkinWebClientBuilderProvider zipkinWebClientBuilderProvider(
        @Value("${trace.auth.username}") String username,
        @Value("${trace.auth.password}") String password,
        @Value("${aurora.klientid:}") String klientid,
        WebClient.Builder builder
    ) {

        return () -> builder.defaultHeaders((headers) -> {
            headers.setBasicAuth(username, password);

            if (klientid != null && klientid.contains("/")) {
                String affiliation = klientid.substring(0, klientid.indexOf("/"));
                headers.set(HEADER_ORGID, affiliation);
            }
        });
    }
}
