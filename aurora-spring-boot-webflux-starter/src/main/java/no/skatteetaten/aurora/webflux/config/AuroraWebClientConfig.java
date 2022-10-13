package no.skatteetaten.aurora.webflux.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.cloud.sleuth.autoconfig.zipkin2.ZipkinAutoConfiguration;
import org.springframework.cloud.sleuth.zipkin2.WebClientSender;
import org.springframework.cloud.sleuth.zipkin2.ZipkinProperties;
import org.springframework.cloud.sleuth.zipkin2.ZipkinWebClientBuilderProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import no.skatteetaten.aurora.webflux.AuroraSpanHandler;
import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer;
import reactor.core.publisher.Mono;

@EnableConfigurationProperties(WebFluxStarterProperties.class)
@Configuration
public class AuroraWebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(AuroraSpanHandler.class);

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

    @Bean(ZipkinAutoConfiguration.SENDER_BEAN_NAME)
    @ConditionalOnProperty(value = "spring.zipkin.enabled", havingValue = "true")
    public WebClientSender webClientSender(
        ZipkinProperties zipkin,
        @Autowired(required = false) ZipkinWebClientBuilderProvider provider
    ) {
        if(provider == null) {
            provider = WebClient::builder;
        }

        return new WebClientSender((response) -> response.onErrorResume((error) -> {
            logger.error(error.getMessage());
            return Mono.empty();
        }),
            provider.zipkinWebClientBuilder().build(),
            zipkin.getBaseUrl(),
            zipkin.getApiPath(),
            zipkin.getEncoder(),
            zipkin.getCheckTimeout());
    }
}
