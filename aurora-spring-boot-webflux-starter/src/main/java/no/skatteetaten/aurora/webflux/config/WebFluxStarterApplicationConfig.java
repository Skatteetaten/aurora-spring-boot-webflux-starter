package no.skatteetaten.aurora.webflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.cloud.sleuth.autoconfig.zipkin2.ZipkinAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.web.HttpServerRequestParser;
import org.springframework.cloud.sleuth.zipkin2.ZipkinProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import brave.http.HttpRequestParser;
import no.skatteetaten.aurora.webflux.AuroraRequestParser;
import no.skatteetaten.aurora.webflux.AuroraSpanHandler;
import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer;
import no.skatteetaten.aurora.webflux.AuroraZipkinWebClientSender;
import zipkin2.reporter.Sender;

@EnableConfigurationProperties({ZipkinProperties.class, WebFluxStarterProperties.class})
@Configuration
public class WebFluxStarterApplicationConfig {

    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.webclient.interceptor", name = "enabled")
    public WebClientCustomizer webClientCustomizer(
        @Value("${spring.application.name:}") String appName,
        @Value("${app.version:}") String appVersion,
        @Value("${aurora.klientid:}") String klientIdEnv
    ) {
        String fallbackKlientId = !appVersion.isBlank() ? String.format("%s/%s", appName, appVersion) : appName;
        String klientId = !klientIdEnv.isBlank() ? klientIdEnv : fallbackKlientId;
        return new AuroraWebClientCustomizer(klientId);
    }

    @Bean(ZipkinAutoConfiguration.SENDER_BEAN_NAME)
    @Primary
    public Sender zipkinSender(ZipkinProperties zipkin, WebClient.Builder builder) {
        return new AuroraZipkinWebClientSender(builder.build(), zipkin.getBaseUrl(), zipkin.getApiPath(), zipkin.getEncoder());
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
}
