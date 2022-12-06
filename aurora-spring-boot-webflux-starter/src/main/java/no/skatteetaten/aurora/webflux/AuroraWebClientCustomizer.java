package no.skatteetaten.aurora.webflux;

import static java.util.UUID.randomUUID;
import static org.springframework.web.reactive.function.client.ClientRequest.from;
import static no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KLIENTID;
import static no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_KORRELASJONSID;
import static no.skatteetaten.aurora.webflux.AuroraConstants.HEADER_MELDINGSID;

import java.util.UUID;

import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import io.opentelemetry.api.baggage.Baggage;

public class AuroraWebClientCustomizer implements WebClientCustomizer {
    private final String name;

    public AuroraWebClientCustomizer(String name) {
        this.name = name;
    }

    @Override
    public void customize(WebClient.Builder builder) {
        builder
            .defaultHeader(HttpHeaders.USER_AGENT, name)
            .defaultHeader(HEADER_KLIENTID, name)
            .filter((request, next) -> next.exchange(
                from(request)
                    .header(HEADER_MELDINGSID, randomUUID().toString())
                    .header(HEADER_KORRELASJONSID, addCorrelationId())
                    .build()
            ));
    }

    protected String addCorrelationId() {
        String korrId = Baggage.current().getEntryValue(HEADER_KORRELASJONSID);
        if (korrId == null) {
            return UUID.randomUUID().toString();
        } else {
            return korrId;
        }
    }
}
