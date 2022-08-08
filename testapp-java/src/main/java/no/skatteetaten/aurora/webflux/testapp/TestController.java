package no.skatteetaten.aurora.webflux.testapp;

import static no.skatteetaten.aurora.webflux.AuroraRequestParser.KORRELASJONSID_FIELD;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import brave.baggage.BaggageField;
import reactor.core.publisher.Mono;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    private final WebClient webClient;

    public TestController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public Mono<Map<String, Object>> get() {
        BaggageField korrelasjonsid = BaggageField.getByName(KORRELASJONSID_FIELD);
        if(!korrelasjonsid.getValue().equals(MDC.get(KORRELASJONSID_FIELD))) {
            throw new IllegalStateException("Korrelasjonsid from baggage does not match value from mdc");
        }

        return webClient.get()
            .uri("/headers")
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() { })
            .map((v) -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("Korrelasjonsid fra innkommende request", korrelasjonsid);
                map.put("Request headers fra WebClient", v);
                return map;
            });
    }

    @GetMapping("/headers")
    public Map<String, String> headers(@RequestHeader HttpHeaders headers) {
        String korrelasjonsid = MDC.get(KORRELASJONSID_FIELD);
        logger.info("MDC: {}", korrelasjonsid);
        Map<String, String> map = new HashMap<>(headers.toSingleValueMap());
        map.put("MDC-" + KORRELASJONSID_FIELD, korrelasjonsid);
        return map;
    }


}
