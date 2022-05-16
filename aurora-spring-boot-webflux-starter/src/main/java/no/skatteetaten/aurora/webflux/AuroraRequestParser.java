package no.skatteetaten.aurora.webflux;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brave.SpanCustomizer;
import brave.baggage.BaggageField;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.propagation.TraceContext;

public class AuroraRequestParser implements HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(AuroraRequestParser.class);

    public static final String USER_AGENT_FIELD = "User-Agent";
    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGSID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    private static final String TRACE_TAG_PREFIX = "aurora.";
    private static final String TRACE_TAG_KORRELASJONS_ID = TRACE_TAG_PREFIX + KORRELASJONSID_FIELD.toLowerCase();
    private static final String TRACE_TAG_KLIENT_ID = TRACE_TAG_PREFIX + KLIENTID_FIELD.toLowerCase();
    private static final String TRACE_TAG_CLUSTER = TRACE_TAG_PREFIX + "CLUSTER";

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        HttpRequestParser.DEFAULT.parse(req, context, span);
        logger.debug("populating MDC fields");

        String meldingsid = req.header(MELDINGSID_FIELD);
        if (meldingsid != null) {
            BaggageField.create(MELDINGSID_FIELD).updateValue(context, meldingsid);
        }

        String klientid = req.header(KLIENTID_FIELD);
        if (klientid != null) {
            BaggageField.create(KLIENTID_FIELD).updateValue(context, klientid);
            span.tag(TRACE_TAG_KLIENT_ID, klientid);
        }

        String korrelasjonsid = Optional.ofNullable(req.header(KORRELASJONSID_FIELD)).orElse(UUID.randomUUID().toString());
        BaggageField.create(KORRELASJONSID_FIELD).updateValue(context, korrelasjonsid);
        span.tag(TRACE_TAG_KORRELASJONS_ID, korrelasjonsid);

        String cluster = Optional.ofNullable(System.getenv("OPENSHIFT_CLUSTER")).orElse("");
        span.tag(TRACE_TAG_CLUSTER, cluster);
    }
}
