package no.skatteetaten.aurora.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import reactor.core.publisher.Hooks;

public class AuroraTraceErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuroraTraceErrorHandler.class);
    private final String baseUrl;

    public AuroraTraceErrorHandler(String baseUrl) {
        this.baseUrl = baseUrl;
        Hooks.onErrorDropped(error -> {
            Throwable cause = error.getCause();
            if (isTraceError(cause)) {
                logger.warn(error.getMessage());
            } else {
                logger.error(error.getMessage(), error);
            }
        });
    }

    Boolean isTraceError(Throwable cause) {
        return (cause instanceof WebClientRequestException
            && ((WebClientRequestException) cause).getUri().toString().startsWith(baseUrl));
    }
}
