package no.skatteetaten.aurora.webflux;

import static org.springframework.cloud.sleuth.autoconfig.instrument.web.SleuthWebProperties.TRACING_FILTER_ORDER;

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
public class AuroraWebFilter implements WebFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuroraWebFilter.class);

    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGSID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    static final String TRACE_TAG_PREFIX = "skatteetaten.";
    static final String TRACE_TAG_KORRELASJONS_ID = TRACE_TAG_PREFIX + KORRELASJONSID_FIELD.toLowerCase();
    static final String TRACE_TAG_KLIENT_ID = TRACE_TAG_PREFIX + KLIENTID_FIELD.toLowerCase();

    private final Tracer tracer;

    public AuroraWebFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    @NonNull
    @SuppressWarnings("resource")
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        Span span = tracer.currentSpan();
        if (span != null) {
            logger.debug("Request: {} {}, SpanId: {}", exchange.getRequest().getMethod(), exchange.getRequest().getPath(), span.context().spanId());
            HttpHeaders headers = exchange.getRequest().getHeaders();

            String meldingsid = headers.getFirst(MELDINGSID_FIELD);
            if (meldingsid != null) {
                tracer.createBaggage(MELDINGSID_FIELD, meldingsid);
            }

            String klientid = headers.getFirst(KLIENTID_FIELD);
            if (klientid != null) {
                tracer.createBaggage(KLIENTID_FIELD, klientid);
                span.tag(TRACE_TAG_KLIENT_ID, klientid);
            }

            String korrelasjonsid = Optional.ofNullable(headers.getFirst(KORRELASJONSID_FIELD))
                .orElse(UUID.randomUUID().toString());
            tracer.createBaggage(KORRELASJONSID_FIELD, korrelasjonsid);
            span.tag(TRACE_TAG_KORRELASJONS_ID, korrelasjonsid);

            logger.debug("All baggage: {}", tracer.getAllBaggage());
        }

        return chain.filter(exchange);
    }
}
