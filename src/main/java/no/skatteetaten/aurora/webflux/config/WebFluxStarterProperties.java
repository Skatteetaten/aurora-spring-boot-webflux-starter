package no.skatteetaten.aurora.webflux.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aurora.webflux.header")
public class WebFluxStarterProperties {
    private AuroraPropsFilter filter;
    private AuroraPropsWebClient webclient;
    private AuroraPropsSpan span;

    public AuroraPropsFilter getFilter() {
        return filter;
    }

    public void setFilter(AuroraPropsFilter filter) {
        this.filter = filter;
    }

    public AuroraPropsWebClient getWebClient() {
        return webclient;
    }

    public void setWebClient(AuroraPropsWebClient webclient) {
        this.webclient = webclient;
    }

    public AuroraPropsSpan getSpan() {
        return span;
    }

    public void setSpan(AuroraPropsSpan span) {
        this.span = span;
    }

    public static class AuroraPropsFilter {
        private boolean enabled;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class AuroraPropsWebClient {
        private AuroraPropsWebClientInterceptor interceptor;

        public AuroraPropsWebClientInterceptor getInterceptor() {
            return interceptor;
        }

        public void setInterceptor(
            AuroraPropsWebClientInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public static class AuroraPropsWebClientInterceptor {
            private boolean enabled;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }

    public static class AuroraPropsSpan {
        private AuroraPropsSpanInterceptor interceptor;

        public AuroraPropsSpanInterceptor getInterceptor() {
            return interceptor;
        }

        public void setInterceptor(
            AuroraPropsSpanInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        public static class AuroraPropsSpanInterceptor {
            private boolean enabled;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
}