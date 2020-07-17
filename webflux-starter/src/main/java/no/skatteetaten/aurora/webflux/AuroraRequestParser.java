package no.skatteetaten.aurora.webflux;

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
    public static final String MELDINGID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    public static final String TAG_KORRELASJONS_ID = "aurora." + KORRELASJONSID_FIELD.toLowerCase();

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        HttpRequestParser.DEFAULT.parse(req, context, span);
        logger.debug("populating MDC fields");

        String meldingsid = req.header(MELDINGID_FIELD);
        if (meldingsid != null) {
            BaggageField.create(MELDINGID_FIELD).updateValue(context, meldingsid);
        }

        String klientid = req.header(KLIENTID_FIELD);
        if (klientid != null) {
            BaggageField.create(KLIENTID_FIELD).updateValue(context, klientid);
        }

        String korrelasjonsid = req.header(KORRELASJONSID_FIELD);
        if (korrelasjonsid == null) {
            korrelasjonsid = UUID.randomUUID().toString();
        }

        BaggageField.create(KORRELASJONSID_FIELD).updateValue(context, korrelasjonsid);
        span.tag(TAG_KORRELASJONS_ID, korrelasjonsid);
    }
}
