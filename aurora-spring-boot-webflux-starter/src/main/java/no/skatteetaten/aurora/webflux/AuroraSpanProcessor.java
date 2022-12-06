package no.skatteetaten.aurora.webflux;

import static no.skatteetaten.aurora.webflux.AuroraConstants.TRACE_TAG_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

public class AuroraSpanProcessor implements SpanProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AuroraSpanProcessor.class);

    static final String TRACE_TAG_CLUSTER = TRACE_TAG_PREFIX + "cluster";
    static final String TRACE_TAG_POD = TRACE_TAG_PREFIX + "pod";
    static final String TRACE_TAG_ENVIRONMENT = TRACE_TAG_PREFIX + "environment";
    static final String TRACE_TAG_AURORA_KLIENTID = TRACE_TAG_PREFIX + "aurora.klientid";

    private final String cluster;
    private final String podName;
    private final String klientid;
    private final String environment;

    public AuroraSpanProcessor(String cluster, String podName, String klientid, String environment) {
        logger.debug("Starting the Aurora span handler");
        this.cluster = cluster;
        this.podName = podName;
        this.klientid = klientid;
        this.environment = environment;
    }

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
        if (hasValue(cluster)) {
            span.setAttribute(TRACE_TAG_CLUSTER, cluster);
        }

        if (hasValue(podName)) {
            span.setAttribute(TRACE_TAG_POD, podName);
        }

        if (hasValue(klientid)) {
            span.setAttribute(TRACE_TAG_AURORA_KLIENTID, klientid);
        }

        if (hasValue(environment)) {
            span.setAttribute(TRACE_TAG_ENVIRONMENT, environment);
        }
    }

    private boolean hasValue(String s) {
        return (s != null && !s.isEmpty());
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan span) {
    }

    @Override
    public boolean isEndRequired() {
        return true;
    }

}
