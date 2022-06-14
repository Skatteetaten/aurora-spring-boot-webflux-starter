package no.skatteetaten.aurora.webflux;

import static no.skatteetaten.aurora.webflux.AuroraRequestParser.TRACE_TAG_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;

public class AuroraSpanHandler extends SpanHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuroraRequestParser.class);

    static final String TRACE_TAG_CLUSTER = TRACE_TAG_PREFIX + "cluster";
    static final String TRACE_TAG_POD = TRACE_TAG_PREFIX + "pod";
    static final String TRACE_TAG_AURORA_KLIENTID = TRACE_TAG_PREFIX + "aurora.klientid";

    private final String cluster;
    private final String podName;
    private final String klientid;

    public AuroraSpanHandler(String cluster, String podName, String klientid) {
        logger.debug("Starting the Aurora span handler");
        this.cluster = cluster;
        this.podName = podName;
        this.klientid = klientid;
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        if (hasValue(cluster)) {
            span.tag(TRACE_TAG_CLUSTER, cluster);
        }

        if (hasValue(podName)) {
            span.tag(TRACE_TAG_POD, podName);
        }

        if (hasValue(klientid)) {
            span.tag(TRACE_TAG_AURORA_KLIENTID, klientid);
        }

        return super.end(context, span, cause);
    }

    private boolean hasValue(String s) {
        return (s != null && !s.isEmpty());
    }
}
