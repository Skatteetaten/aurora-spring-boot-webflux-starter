package no.skatteetaten.aurora.webflux;

import static org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties.TRACING_FILTER_ORDER;
import static no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KLIENTID;
import static no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KORRELASJONSID;
import static no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_MELDINGSID;
import static no.skatteetaten.aurora.webflux.AuroraConstants.TRACE_TAG_KLIENT_ID;
import static no.skatteetaten.aurora.webflux.AuroraConstants.TRACE_TAG_KORRELASJONS_ID;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(TRACING_FILTER_ORDER + 5)
public class AuroraHeaderWebFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuroraHeaderWebFilter.class);

    private final Tracer tracer;

    public AuroraHeaderWebFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    @NonNull
    @SuppressWarnings("resource")
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        Span span = tracer.currentSpan();
        if (span != null) {
            HttpHeaders headers = exchange.getRequest().getHeaders();

            String meldingsid = headers.getFirst(HEADER_MELDINGSID);
            if (meldingsid != null) {
                tracer.createBaggage(HEADER_MELDINGSID, meldingsid);
            }

            String klientid = headers.getFirst(HEADER_KLIENTID);
            if (klientid != null) {
                tracer.createBaggage(HEADER_KLIENTID, klientid);
                span.tag(TRACE_TAG_KLIENT_ID, klientid);
            }

            String korrelasjonsid = Optional.ofNullable(headers.getFirst(HEADER_KORRELASJONSID))
                .orElse(UUID.randomUUID().toString());
            tracer.createBaggage(HEADER_KORRELASJONSID, korrelasjonsid);
            span.tag(TRACE_TAG_KORRELASJONS_ID, korrelasjonsid);

            logger.debug("All baggage: {}", tracer.getAllBaggage());
        }

        return chain.filter(exchange);
    }
}
