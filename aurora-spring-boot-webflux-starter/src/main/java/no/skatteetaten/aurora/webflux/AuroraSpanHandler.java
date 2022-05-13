package no.skatteetaten.aurora.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brave.handler.SpanHandler;

public class AuroraSpanHandler extends SpanHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuroraRequestParser.class);

    public AuroraSpanHandler() {
        logger.debug("Starting the Aurora noop span handler");
    }
}
