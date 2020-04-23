package no.skatteetaten.aurora.webflux;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import brave.propagation.ExtraFieldPropagation;
import reactor.core.publisher.Mono;

public class AuroraHeaderWebFilter implements WebFilter, Ordered {
    public static final String USER_AGENT_FIELD = "User-Agent";
    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        MDC.remove(USER_AGENT_FIELD);
        MDC.remove(KLIENTID_FIELD);
        MDC.remove(MELDINGID_FIELD);
        MDC.remove(KORRELASJONSID_FIELD);

        HttpHeaders headers = exchange.getRequest().getHeaders();

        String klientId = headers.getFirst(KLIENTID_FIELD);
        if (klientId == null) {
            klientId = headers.getFirst(USER_AGENT_FIELD);
        }
        ExtraFieldPropagation.set(KLIENTID_FIELD, klientId);
        MDC.put(KLIENTID_FIELD, klientId);

        String userAgent = headers.getFirst(USER_AGENT_FIELD);
        ExtraFieldPropagation.set(USER_AGENT_FIELD, userAgent);
        MDC.put(USER_AGENT_FIELD, userAgent);

        String meldingsId = headers.getFirst(MELDINGID_FIELD);
        if (meldingsId == null) {
            meldingsId = UUID.randomUUID().toString();
        }
        ExtraFieldPropagation.set(MELDINGID_FIELD, meldingsId);
        MDC.put(MELDINGID_FIELD, meldingsId);

        return null;
    }
}
