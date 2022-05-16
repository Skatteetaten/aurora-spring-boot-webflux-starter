package no.skatteetaten.aurora.webflux.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aurora.webflux.header")
public class WebFluxStarterProperties {
    private AuroraPropsFilter filter;
    private AuroraPropsWebclient webclient;

    public AuroraPropsFilter getFilter() {
        return filter;
    }

    public void setFilter(AuroraPropsFilter filter) {
        this.filter = filter;
    }

    public AuroraPropsWebclient getWebclient() {
        return webclient;
    }

    public void setWebclient(AuroraPropsWebclient webclient) {
        this.webclient = webclient;
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

    public static class AuroraPropsWebclient {
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
}
