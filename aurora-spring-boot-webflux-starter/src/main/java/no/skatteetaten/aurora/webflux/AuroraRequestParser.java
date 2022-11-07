package no.skatteetaten.aurora.webflux;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.SpanCustomizer;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.http.HttpRequest;
import org.springframework.cloud.sleuth.http.HttpRequestParser;

public class AuroraRequestParser implements HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(AuroraRequestParser.class);

    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGSID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    static final String TRACE_TAG_PREFIX = "skatteetaten.";
    static final String TRACE_TAG_KORRELASJONS_ID = TRACE_TAG_PREFIX + KORRELASJONSID_FIELD.toLowerCase();
    static final String TRACE_TAG_KLIENT_ID = TRACE_TAG_PREFIX + KLIENTID_FIELD.toLowerCase();

    private final Tracer tracer;

    public AuroraRequestParser(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        // HttpRequestParser.DEFAULT.parse(req, context, span);
        logger.debug("populating MDC fields");

        String meldingsid = req.header(MELDINGSID_FIELD);
        if (meldingsid != null) {
            tracer.createBaggage(MELDINGSID_FIELD, meldingsid);
        }

        String klientid = req.header(KLIENTID_FIELD);
        if (klientid != null) {
            tracer.getBaggage(KLIENTID_FIELD).set(klientid);
            //MDC.put(KLIENTID_FIELD, klientid);
            span.tag(TRACE_TAG_KLIENT_ID, klientid);
        }

        String korrelasjonsid = Optional.ofNullable(req.header(KORRELASJONSID_FIELD))
            .orElse(UUID.randomUUID().toString());

        logger.info("Korrid: " + korrelasjonsid);


        tracer.createBaggage("1", "1");
        tracer.createBaggage("2", "2");
        logger.info("All baggage: "  + tracer.getAllBaggage());

        tracer.getBaggage(context, KLIENTID_FIELD).set(klientid);
        tracer.getBaggage(context, KORRELASJONSID_FIELD).set(korrelasjonsid);
        span.tag(TRACE_TAG_KORRELASJONS_ID, korrelasjonsid);
    }
}
