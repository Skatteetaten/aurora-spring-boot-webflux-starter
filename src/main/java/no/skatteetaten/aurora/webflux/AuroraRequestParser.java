package no.skatteetaten.aurora.webflux;

import static no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KORRELASJONSID_FIELD;

import java.util.UUID;

import brave.SpanCustomizer;
import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;

public class AuroraRequestParser implements HttpRequestParser {
    public static final String TAG_KORRELASJONS_ID = "aurora." + KORRELASJONSID_FIELD.toLowerCase();

    @Override
    public void parse(HttpRequest req, TraceContext context, SpanCustomizer span) {
        HttpRequestParser.DEFAULT.parse(req, context, span);

        String korrelasjonsid = req.header(KORRELASJONSID_FIELD);
        if (korrelasjonsid == null) {
            korrelasjonsid = UUID.randomUUID().toString();
        }

        ExtraFieldPropagation.set(context, KORRELASJONSID_FIELD, korrelasjonsid);
        span.tag(TAG_KORRELASJONS_ID, korrelasjonsid);
    }
}
