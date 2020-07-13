package no.skatteetaten.aurora.webflux;

import static java.util.UUID.randomUUID;
import static org.springframework.web.reactive.function.client.ClientRequest.from;

import static no.skatteetaten.aurora.webflux.AuroraRequestParser.KLIENTID_FIELD;
import static no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD;
import static no.skatteetaten.aurora.webflux.AuroraRequestParser.MELDINGID_FIELD;
import static no.skatteetaten.aurora.webflux.AuroraRequestParser.USER_AGENT_FIELD;

import java.util.UUID;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.web.reactive.function.client.WebClient;

import brave.baggage.BaggageField;

public class AuroraWebClientCustomizer implements WebClientCustomizer {
    private final String name;

    public AuroraWebClientCustomizer(String name) {
        this.name = name;
    }

    @Override
    public void customize(WebClient.Builder builder) {
        builder
            .defaultHeader(USER_AGENT_FIELD, name)
            .defaultHeader(KLIENTID_FIELD, name)
            .filter((request, next) -> next.exchange(
                from(request)
                    .header(MELDINGID_FIELD, randomUUID().toString())
                    .header(KORRELASJONSID_FIELD, getKorrelasjonsid())
                    .build()
            ));
    }

    private String getKorrelasjonsid() {
        BaggageField field = BaggageField.getByName(KORRELASJONSID_FIELD);
        if (field == null) {
            return UUID.randomUUID().toString();
        }
        return field.getValue();
    }
}
