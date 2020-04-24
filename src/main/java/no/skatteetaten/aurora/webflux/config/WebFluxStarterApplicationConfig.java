package no.skatteetaten.aurora.webflux.config;

import static brave.propagation.ExtraFieldPropagation.get;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter;
import no.skatteetaten.aurora.webflux.AuroraWebSpanCustomizer;
import no.skatteetaten.aurora.webflux.AuroraWebClientCustomizer;

@Configuration
@EnableConfigurationProperties(WebFluxStarterProperties.class)
public class WebFluxStarterApplicationConfig {

    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.filter", name = "enabled", matchIfMissing = true)
    public AuroraHeaderWebFilter auroraHeaderWebFilter() {
        return new AuroraHeaderWebFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.webclient.interceptor", name = "enabled")
    public WebClientCustomizer userAgentWebClientCustomizer(@Value("${spring.application.name}") String name) {
        return new AuroraWebClientCustomizer(name);
    }

    @Bean
    @ConditionalOnProperty(prefix = "aurora.webflux.header.span.interceptor", name = "enabled", matchIfMissing = true)
    public AuroraWebSpanCustomizer auroraSpanCustomizer() {
        return new AuroraWebSpanCustomizer();
    }
}
