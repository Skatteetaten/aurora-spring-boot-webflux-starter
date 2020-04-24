package no.skatteetaten.aurora.webflux;

import static no.skatteetaten.aurora.webflux.AuroraHeaderWebFilter.KORRELASJONSID_FIELD;

import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;

public class AuroraWebSpanCustomizer extends FinishedSpanHandler {

    public static final String TAG_KORRELASJONS_ID = "aurora." + KORRELASJONSID_FIELD.toLowerCase();

    @Override
    public boolean handle(TraceContext context, MutableSpan span) {
        String korrelasjonsId = ExtraFieldPropagation.get(context, KORRELASJONSID_FIELD);
        if (korrelasjonsId != null) {
            span.tag(TAG_KORRELASJONS_ID, korrelasjonsId);
        }

        return true;
    }
}
