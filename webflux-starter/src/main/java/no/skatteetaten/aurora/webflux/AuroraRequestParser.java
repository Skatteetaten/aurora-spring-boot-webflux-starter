package no.skatteetaten.aurora.webflux;

import java.util.UUID;

import brave.SpanCustomizer;
import brave.baggage.BaggageField;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.propagation.TraceContext;

public class AuroraRequestParser implements HttpRequestParser {
    public static final String USER_AGENT_FIELD = "User-Agent";
    public static final String KORRELASJONSID_FIELD = "Korrelasjonsid";
    public static final String MELDINGID_FIELD = "Meldingsid";
    public static final String KLIENTID_FIELD = "Klientid";

    public static final String TAG_KORRELASJONS_ID = "aurora." + KORRELASJONSID_FIELD.toLowerCase();
    private final String name;

    public AuroraRequestParser(String name) {
        this.name = name;
    }

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        HttpRequestParser.DEFAULT.parse(req, context, span);

        BaggageField.create(MELDINGID_FIELD).updateValue(context, UUID.randomUUID().toString());
        BaggageField.create(KLIENTID_FIELD).updateValue(context, name);

        String korrelasjonsid = req.header(KORRELASJONSID_FIELD);
        if (korrelasjonsid == null) {
            korrelasjonsid = UUID.randomUUID().toString();
        }

        BaggageField.create(KORRELASJONSID_FIELD).updateValue(context, korrelasjonsid);
        span.tag(TAG_KORRELASJONS_ID, korrelasjonsid);
    }
}
