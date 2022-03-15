package no.skatteetaten.aurora.webflux;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.SpanCustomizer;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.http.HttpRequest;
import org.springframework.cloud.sleuth.http.HttpRequestParser;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageBuilder;
import io.opentelemetry.context.Context;

public class AuroraRequestParser implements HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(AuroraRequestParser.class);

    public static final String USER_AGENT_FIELD = "User-Agent";
    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGSID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    public static final String TAG_KORRELASJONS_ID = "aurora." + KORRELASJONSID_FIELD.toLowerCase();

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        logger.debug("populating MDC fields");
        BaggageBuilder baggageBuilder = Baggage.current().toBuilder();

        String meldingsid = req.header(MELDINGSID_FIELD);
        if (meldingsid != null) {
            baggageBuilder.put(MELDINGSID_FIELD, meldingsid);
        }

        String klientid = req.header(KLIENTID_FIELD);
        if (klientid != null) {
            baggageBuilder.put(KLIENTID_FIELD, klientid);
        }

        String korrelasjonsid = req.header(KORRELASJONSID_FIELD);
        if (korrelasjonsid == null) {
            korrelasjonsid = UUID.randomUUID().toString();
        }

        baggageBuilder.put(KORRELASJONSID_FIELD, korrelasjonsid).build().makeCurrent();
        span.tag(TAG_KORRELASJONS_ID, korrelasjonsid);
    }
}
