package no.skatteetaten.aurora.webflux;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.cloud.sleuth.instrument.web.TraceWebFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import brave.Span;
import brave.propagation.ExtraFieldPropagation;
import reactor.core.publisher.Mono;

public class AuroraHeaderWebFilter implements WebFilter, Ordered {
    public static final String USER_AGENT_FIELD = "User-Agent";
    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    @Override
    public int getOrder() {
        return TraceWebFilter.ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        setFieldValue(exchange, KLIENTID_FIELD, USER_AGENT_FIELD);
        setFieldValue(exchange, MELDINGID_FIELD, UUID.randomUUID().toString());
        setFieldValue(exchange, KORRELASJONSID_FIELD, getKorrelasjonsid(exchange));

        return chain.filter(exchange);
    }

    private void setFieldValue(ServerWebExchange exchange, String fieldName, String value) {
        MDC.remove(fieldName);
        String headerValue = exchange.getRequest().getHeaders().getFirst(fieldName);
        if (headerValue == null) {
            headerValue = value;
        }
        MDC.put(fieldName, headerValue);
    }

    private String getKorrelasjonsid(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String korrelasjonsId = headers.getFirst(KORRELASJONSID_FIELD);
        if (korrelasjonsId != null) {
            return korrelasjonsId;
        }

        Span currentSpan = exchange.getAttribute(TraceWebFilter.class.getName() + ".TRACE");
        if (currentSpan != null && currentSpan.context() != null) {
            String spanKorrId =
                ExtraFieldPropagation.get(currentSpan.context(), KORRELASJONSID_FIELD);
            if (spanKorrId != null) {
                return spanKorrId;
            }
        }

        return UUID.randomUUID().toString();
    }
}
