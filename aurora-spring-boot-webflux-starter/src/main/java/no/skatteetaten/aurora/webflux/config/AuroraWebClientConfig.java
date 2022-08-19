package no.skatteetaten.aurora.webflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer;

@EnableConfigurationProperties(WebFluxStarterProperties.class)
@Configuration
public class AuroraWebClientConfig {

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
}
