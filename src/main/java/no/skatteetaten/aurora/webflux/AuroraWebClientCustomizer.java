package no.skatteetaten.aurora.webflux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;

import static brave.propagation.ExtraFieldPropagation.get;
import static java.util.UUID.randomUUID;
import static no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KLIENTID_FIELD;
import static no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KORRELASJONSID_FIELD;
import static no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.MELDINGID_FIELD;
import static no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.USER_AGENT_FIELD;
import static org.springframework.web.reactive.function.client.ClientRequest.from;

public class AuroraWebClientCustomizer {
    @Bean
    @ConditionalOnProperty(
        prefix = "aurora.webflux.header.webclient.interceptor",
        name = "enabled",
        matchIfMissing = true)
    public WebClientCustomizer userAgentWebClientCustomizer(@Value("${spring.application.name}") String name) {
        return webClientBuilder -> webClientBuilder
            .defaultHeader(USER_AGENT_FIELD, name)
            .defaultHeader(KLIENTID_FIELD, name)
            .filter((request, next) -> next.exchange(
                from(request)
                    .header(MELDINGID_FIELD, randomUUID().toString())
                    .header(KORRELASJONSID_FIELD, get(KORRELASJONSID_FIELD))
                    .build()
            ));
    }
}
