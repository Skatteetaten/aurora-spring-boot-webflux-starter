package no.skatteetaten.aurora.webflux;

public final class AuroraConstants {
    public static final String HEADER_KORRELASJONSID = "Korrelasjonsid";
    public static final String HEADER_MELDINGSID = "Meldingsid";
    public static final String HEADER_KLIENTID = "Klientid";

    static final String TRACE_TAG_PREFIX = "skatteetaten.";
    static final String TRACE_TAG_KORRELASJONS_ID = TRACE_TAG_PREFIX + HEADER_KORRELASJONSID.toLowerCase();
    static final String TRACE_TAG_KLIENT_ID = TRACE_TAG_PREFIX + HEADER_KLIENTID.toLowerCase();

    private AuroraConstants() { }
}
