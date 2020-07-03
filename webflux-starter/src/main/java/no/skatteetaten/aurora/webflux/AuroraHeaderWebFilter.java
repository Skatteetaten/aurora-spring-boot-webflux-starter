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

    private final String name;

    public AuroraHeaderWebFilter(String name) {
        this.name = name;
    }

    @Override
    public int getOrder() {
        return TraceWebFilter.ORDER + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        AuroraField.create(exchange).withName(USER_AGENT_FIELD).withFixedValue(name);
        AuroraField.create(exchange).withName(KLIENTID_FIELD).withFixedValue(name);
        AuroraField.create(exchange).withName(MELDINGID_FIELD).withGeneratedId();
        AuroraField.create(exchange).withName(KORRELASJONSID_FIELD).withKorrelasjonsid();
        return chain.filter(exchange);
    }

    static class AuroraField {
        private final ServerWebExchange exchange;
        private String name;

        AuroraField(ServerWebExchange exchange) {
            this.exchange = exchange;
        }

        AuroraField withName(String name) {
            this.name = name;
            return this;
        }

        void withFixedValue(String value) {
            if (value != null) {
                MDC.remove(name);
                MDC.put(name, value);
            }
        }

        void withValue(String value) {
            MDC.remove(name);
            String headerValue = exchange.getRequest().getHeaders().getFirst(name);
            if (headerValue == null) {
                headerValue = value;
            }
            MDC.put(name, headerValue);
        }

        void withGeneratedId() {
            withValue(UUID.randomUUID().toString());
        }

        void withKorrelasjonsid() {
            withValue(getKorrelasjonsid());
        }

        private String getKorrelasjonsid() {
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

        static AuroraField create(ServerWebExchange exchange) {
            return new AuroraField(exchange);
        }
    }
}
